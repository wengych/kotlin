/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.js;

import com.intellij.ProjectTopics;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.EmptyModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.kotlin.idea.framework.KotlinJavascriptLibraryDetectionUtil;
import org.jetbrains.kotlin.idea.project.ProjectStructureUtil;
import org.jetbrains.kotlin.utils.LibraryUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class KotlinJavascriptLibraryManager implements ProjectComponent, ModuleRootListener {

    public static KotlinJavascriptLibraryManager getInstance(@NotNull Project project) {
        return project.getComponent(KotlinJavascriptLibraryManager.class);
    }

    public static final String LIBRARY_NAME = "<Kotlin JavaScript library>";
    private final Object LIBRARY_COMMIT_LOCK = new Object();

    private Project myProject;

    private final AtomicBoolean myMuted = new AtomicBoolean(false);

    @SuppressWarnings("UnusedDeclaration")
    private KotlinJavascriptLibraryManager(@NotNull Project project) {
        myProject = project;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "KotlinJavascriptLibraryManager";
    }

    @Override
    public void projectOpened() {
        myProject.getMessageBus().connect(myProject).subscribe(ProjectTopics.PROJECT_ROOTS, this);
        DumbService.getInstance(myProject).smartInvokeLater(new Runnable() {
            @Override
            public void run() {
                updateProjectLibrary();
            }
        });
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
        myProject = null;
    }

    @Override
    public void beforeRootsChange(ModuleRootEvent event) {
    }

    @Override
    public void rootsChanged(ModuleRootEvent event) {
        if (myMuted.get()) return;
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                DumbService.getInstance(myProject).runWhenSmart(new Runnable() {
                    @Override
                    public void run() {
                        updateProjectLibrary();
                    }
                });
            }
        }, ModalityState.NON_MODAL, myProject.getDisposed());
    }

    @TestOnly
    public void syncUpdateProjectLibrary() {
        updateProjectLibrary(true);
    }

    /**
     * @param synchronously may be true only in tests.
     */
    private void updateProjectLibrary(boolean synchronously) {
        if (myProject == null || myProject.isDisposed()) return;
        ApplicationManager.getApplication().assertReadAccessAllowed();

        boolean testMode = ApplicationManager.getApplication().isUnitTestMode();
        for (Module module : ModuleManager.getInstance(myProject).getModules()) {
            if (!isModuleApplicable(module, testMode)) continue;

            final Collection<VirtualFile> clsRootFiles = new ArrayList<VirtualFile>();
            final Collection<VirtualFile> srcRootFiles = new ArrayList<VirtualFile>();

            if (ProjectStructureUtil.isJsKotlinModule(module)) {
                ModuleRootManager.getInstance(module).orderEntries().librariesOnly().forEachLibrary(new Processor<Library>() {
                    @Override
                    public boolean process(Library library) {
                        VirtualFile[] libFiles = library.getFiles(OrderRootType.CLASSES);
                        VirtualFile[] libSources = library.getFiles(OrderRootType.SOURCES);
                        if (!KotlinJavascriptLibraryDetectionUtil.isKotlinJavascriptLibrary(library)) {
                            return true;
                        }

                        boolean added = false;
                        for (VirtualFile libFile : libFiles) {
                            String path = PathUtil.getLocalPath(libFile);
                            assert path != null;
                            File file = new File(path);
                            if (LibraryUtils.isKotlinJavascriptLibraryWithMetadata(file)) {
                                VirtualFile classRoot = KotlinJavascriptMetaFileSystem.getInstance().refreshAndFindFileByPath(path + "!/");
                                clsRootFiles.add(classRoot);
                                added = true;
                            }
                        }
                        if (added) {
                            srcRootFiles.addAll(new ArrayList<VirtualFile>(Arrays.asList(libSources)));
                        }

                        return true;
                    }
                });
            }

            ChangesToApply changesToApply = new ChangesToApply();

            for (VirtualFile file : clsRootFiles) {
                changesToApply.getClsUrlsToAdd().add(file.getUrl());
            }
            for (VirtualFile sourceFile : srcRootFiles) {
                changesToApply.getSrcUrlsToAdd().add(sourceFile.getUrl());
            }
            resetLibraries(module, changesToApply, LIBRARY_NAME, synchronously);
        }
    }

    private void updateProjectLibrary() {
        updateProjectLibrary(false);
    }

    private static boolean isModuleApplicable(Module module, boolean testMode) {
        String id = ModuleType.get(module).getId();
        return ProjectStructureUtil.isJsKotlinModule(module) ||
               (testMode && EmptyModuleType.EMPTY_MODULE.equals(id));
    }

    public void resetLibraries(Module module, ChangesToApply changesToApply, String libraryName, boolean synchronously) {
        applyChange(module, changesToApply, true, synchronously, libraryName);
    }

    private void applyChange(final Module module,
            final ChangesToApply changesToApply,
            final boolean reset,
            boolean synchronously, final String libraryName) {
        if (synchronously) {    //for test only
            Application application = ApplicationManager.getApplication();
            if (!application.isUnitTestMode()) {
                throw new IllegalStateException("Synchronous library update may be done only in test mode");
            }
            AccessToken token = application.acquireWriteActionLock(KotlinJavascriptLibraryManager.class);
            try {
                applyChangeImpl(module, changesToApply, reset, libraryName);
            }
            finally {
                token.finish();
            }
        }
        else {
            final Runnable commit = new Runnable() {
                @Override
                public void run() {
                    applyChangeImpl(module, changesToApply, reset, libraryName);
                }
            };
            Runnable commitInWriteAction = new Runnable() {
                @Override
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(commit);
                }
            };
            ApplicationManager.getApplication().invokeLater(commitInWriteAction, myProject.getDisposed());
        }
    }

    private void applyChangeImpl(Module module, ChangesToApply changesToApply, boolean reset, String libraryName) {
        synchronized (LIBRARY_COMMIT_LOCK) {
            if (module.isDisposed()) {
                return;
            }
            ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
            LibraryTable.ModifiableModel libraryTableModel = model.getModuleLibraryTable().getModifiableModel();

            Library library = findLibraryByName(libraryTableModel, libraryName);

            if (library == null) {
                if (reset && changesToApply.getClsUrlsToAdd().isEmpty()) {
                    model.dispose();
                    return;
                }
                library = libraryTableModel.createLibrary(libraryName);
            }

            if (reset && changesToApply.getClsUrlsToAdd().isEmpty()) {
                libraryTableModel.removeLibrary(library);
                commitLibraries(null, libraryTableModel, model);
                return;
            }

            Library.ModifiableModel libraryModel = library.getModifiableModel();
            if (reset) {
                Set<String> classesUrls = new HashSet<String>(Arrays.asList(library.getUrls(OrderRootType.CLASSES)));
                Set<String> sourcesUrls = new HashSet<String>(Arrays.asList(library.getUrls(OrderRootType.SOURCES)));
                Set<String> existingClsUrls = new HashSet<String>();
                Set<String> existingSrcUrls = new HashSet<String>();
                existingClsUrls.addAll(classesUrls);
                existingSrcUrls.addAll(sourcesUrls);

                if (changesToApply.getClsUrlsToRemove().isEmpty()) {
                    Set<String> newClsUrls = new HashSet<String>(changesToApply.getClsUrlsToAdd());
                    Set<String> newSrcUrls = new HashSet<String>(changesToApply.getSrcUrlsToAdd());
                    if (existingClsUrls.equals(newClsUrls) && existingSrcUrls.equals(newSrcUrls)) {
                        model.dispose();
                        Disposer.dispose(libraryModel);
                        return;
                    }
                }

                for (String url : classesUrls) {
                    libraryModel.removeRoot(url, OrderRootType.CLASSES);
                }
                for (String url : sourcesUrls) {
                    libraryModel.removeRoot(url, OrderRootType.SOURCES);
                }
            }

            for (String url : changesToApply.getClsUrlsToRemove()) {
                libraryModel.removeRoot(url, OrderRootType.CLASSES);
                libraryModel.removeRoot(url, OrderRootType.SOURCES);
            }
            for (String url : changesToApply.getSrcUrlsToRemove()) {
                libraryModel.removeRoot(url, OrderRootType.SOURCES);
            }
            for (String url : changesToApply.getClsUrlsToAdd()) {
                libraryModel.addRoot(url, OrderRootType.CLASSES);
            }
            for (String url : changesToApply.getSrcUrlsToAdd()) {
                libraryModel.addRoot(url, OrderRootType.SOURCES);
            }
            commitLibraries(libraryModel, libraryTableModel, model);
        }
    }

    private void commitLibraries(@Nullable Library.ModifiableModel libraryModel,
            LibraryTable.ModifiableModel tableModel,
            ModifiableRootModel model) {
        try {
            myMuted.set(true);
            if (libraryModel != null) {
                libraryModel.commit();
            }
            tableModel.commit();
            model.commit();
        }
        finally {
            myMuted.set(false);
        }
    }

    @Nullable
    private static Library findLibraryByName(LibraryTable.ModifiableModel libraryTableModel, String suggestedLibraryName) {
        for (Library library : libraryTableModel.getLibraries()) {
            String libraryName = library.getName();
            if (libraryName != null && libraryName.startsWith(suggestedLibraryName)) {
                return library;
            }
        }
        return null;
    }

    private static class ChangesToApply {
        private final List<String> clsUrlsToAdd = new ArrayList<String>();
        private final List<String> clsUrlsToRemove = new ArrayList<String>();

        private final List<String> srcUrlsToAdd = new ArrayList<String>();
        private final List<String> srcUrlsToRemove = new ArrayList<String>();

        public List<String> getClsUrlsToAdd() {
            return clsUrlsToAdd;
        }

        public List<String> getClsUrlsToRemove() {
            return clsUrlsToRemove;
        }

        public List<String> getSrcUrlsToAdd() {
            return srcUrlsToAdd;
        }

        public List<String> getSrcUrlsToRemove() {
            return srcUrlsToRemove;
        }
    }
}

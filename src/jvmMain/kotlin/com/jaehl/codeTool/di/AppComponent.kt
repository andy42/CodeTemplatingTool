package com.jaehl.codeTool.di

import com.jaehl.codeTool.di.modules.LocalModule
import com.jaehl.codeTool.ui.dialog.folderPicker.FolderPickerDialogComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogComponent
import com.jaehl.codeTool.ui.page.projectEdit.ProjectEditComponent
import com.jaehl.codeTool.ui.page.projectList.ProjectListComponent
import com.jaehl.codeTool.ui.page.templateApply.TemplateApplyPageComponent
import com.jaehl.codeTool.ui.page.templateEdit.TemplateEditComponent
import com.jaehl.codeTool.ui.page.templateList.TemplateListComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        LocalModule::class
    ]
)
interface AppComponent {
    fun inject(projectEditComponent : ProjectEditComponent)
    fun inject(projectListComponent : ProjectListComponent)
    fun inject(templateApplyPageComponent : TemplateApplyPageComponent)
    fun inject(templateEditComponent : TemplateEditComponent)
    fun inject(templateListComponent : TemplateListComponent)

    fun inject(folderPickerDialogComponent : FolderPickerDialogComponent)
    fun inject(warningDialogComponent : WarningDialogComponent)
}
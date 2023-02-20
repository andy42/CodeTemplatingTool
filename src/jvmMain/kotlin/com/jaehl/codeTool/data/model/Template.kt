package com.jaehl.codeTool.data.model

data class TemplateFile(
    val path : String = "",
    val pathDestination : String = ""
)

enum class TemplateVariableType(val value : kotlin.String){
    String("String"),
    Path("Path"),
    Package("Package");

    override fun toString(): kotlin.String {
        return value
    }
}

data class TemplateVariable(
    val name : String = "",
    val type : TemplateVariableType = TemplateVariableType.String,
    val default : String = ""
)

data class TemplateFileOutput(
    val path : String = "",
    val data : String = ""
)

data class Template(
    var id : String = "",
    val name : String = "",
    val dirPath : String = "",
    val variable : List<TemplateVariable> = arrayListOf(),
    val files : List<TemplateFile> = arrayListOf()
) {
    companion object {
        fun tempDialog() : Template {
            return Template(
                id = "1",
                name = "Template Dialog",
                dirPath = "{{USER_DIR}}\\CodeTool\\templateDialog",
                variable = listOf(
                    TemplateVariable(
                        name = "package",
                        type = TemplateVariableType.Path,
                        default = ""
                    ),
                    TemplateVariable(
                        name = "name",
                        type = TemplateVariableType.String,
                        default = ""
                    )
                ),
                files = listOf(
                    TemplateFile(
                        path = "{{TEMPLATE_DIR}}\\templateDialog.txt",
                        pathDestination = "{{packagePath}}\\{{name}}Dialog.kt"
                    )
                )
            )
        }

        fun newPage() : Template {
            return Template(
                id = "0",
                name = "Template Page",
                dirPath = "{{USER_DIR}}\\CodeTool\\templatePage",
                variable = listOf(
                    TemplateVariable(
                        name = "package",
                        type = TemplateVariableType.Package,
                        default = ""
                    ),
                    TemplateVariable(
                        name = "folderName",
                        type = TemplateVariableType.String,
                        default = ""
                    ),
                    TemplateVariable(
                        name = "name",
                        type = TemplateVariableType.String,
                        default = ""
                    )
                ),
                files = listOf(
                    TemplateFile(
                        path = "{{TEMPLATE_DIR}}\\templatePage.txt",
                        pathDestination = "{{packagePath}}\\{{folderName}}\\{{name}}Page.kt"
                    ),
                    TemplateFile(
                        path = "{{TEMPLATE_DIR}}\\templatePageComponent.txt",
                        pathDestination = "{{packagePath}}\\{{folderName}}\\{{name}}PageComponent.kt"
                    ),
                    TemplateFile(
                        path = "{{TEMPLATE_DIR}}\\templateViewModel.txt",
                        pathDestination = "{{packagePath}}\\{{folderName}}\\{{name}}ViewModel.kt"
                    )

                )
            )
        }
    }
}

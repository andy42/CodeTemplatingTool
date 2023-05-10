package com.jaehl.codeTool.di.modules

import com.google.gson.reflect.TypeToken
import com.jaehl.codeTool.Configuration
import com.jaehl.codeTool.ConfigurationImp
import com.jaehl.codeTool.data.local.ObjectListJsonLoader
import com.jaehl.codeTool.data.local.ObjectListLoader
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.repo.ProjectRepo
import com.jaehl.codeTool.data.templateCreator.TemplateCreator
import com.jaehl.codeTool.data.templateCreator.TemplateCreatorImp
import com.jaehl.codeTool.data.templateParser.TemplateParser
import com.jaehl.codeTool.data.templateParser.TemplateParserImp
import com.jaehl.codeTool.ui.page.templateEdit.TemplateEditValidator
import com.jaehl.codeTool.ui.page.templateEdit.TemplateEditValidatorImp
import com.jaehl.codeTool.ui.util.OsPathConverter
import com.jaehl.codeTool.ui.util.OsPathConverterImp
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.FileUtilImp
import com.jaehl.codeTool.util.Logger
import dagger.Module
import dagger.Provides

@Module
class LocalModule {

    @Provides
    fun logger() : Logger {
        return Logger()
    }

    @Provides
    fun configuration() : Configuration {
        return ConfigurationImp()
    }

    @Provides
    fun projectListLoader(logger : Logger, configuration : Configuration) : ObjectListLoader<Project>{
        return ObjectListJsonLoader(
            logger = logger,
            type = object : TypeToken<Array<Project>>() {}.type,
            projectUserDir = configuration.getProjectUserDir(),
            templateListFile = configuration.getProjectListFile())
    }

    @Provides
    fun templateListLoader(logger : Logger, configuration : Configuration) : ObjectListLoader<Template> {
        return ObjectListJsonLoader(
            logger = logger,
            type = object : TypeToken<Array<Template>>() {}.type,
            projectUserDir = configuration.getProjectUserDir(),
            templateListFile = configuration.getTemplateListFile())
    }

    @Provides
    fun osPathConverter() : OsPathConverter {
        return OsPathConverterImp()
    }

    @Provides
    fun fileUtil(fileUtilImp : FileUtilImp) : FileUtil = fileUtilImp

    @Provides
    fun templateEditValidator(validator : TemplateEditValidatorImp) : TemplateEditValidator = validator

    @Provides
    fun templateCreator(templateCreator : TemplateCreatorImp) : TemplateCreator = templateCreator

    @Provides
    fun templateParser(templateParser : TemplateParserImp) : TemplateParser = templateParser
}
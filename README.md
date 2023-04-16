# About
This Code Tool is used to template files with key-value variables in a simple GUI

Applying a template to a project will create files in the selected project, reducing the need to recreate boil plate classes/files every time.

# Template Variables

A Template Variable is used in a template to replace a key String "{{name}}" with a user-supplied value from a text field

## String Variable types
![](/readmeScreenshots/variableString.png)

- "Name" is the Variable key
- "Type" = String
- "Default" is the prepopulated value when using the template, can be left blank

## Path Variable types
![](/readmeScreenshots/variablePath.png)

when using template apply clicking this field will open a folder picker, path is relative to the project root folder
- "name" is the Variable key
- "Type" = Path
- "Start Path" is the directory the Folder picker dialog will open on. can be left blank (relative to the project root folder) can use Variable in this, see screenshot above
- "Default" is the prepopulated value when using the template, can be left blank

## Variable keys

### basic variable key
in a template file use variable names surrounded by "{{" & "}}
for example a Variable called "name" would be "{{name}}"

### Import key
if you have a path (as a Variable String type or Path type) you can suffix the name with "$import"

for example a variable called "mainPackage" would be "{{mainPackage$import}}"

this will replace all path separators with "." but removing the first "." so a path "\com\example\demo" would become "com.exsample.demo"

# User Guide

## project setup

- start program and then on the "Home" page click "Add Project"

### Project settings 
- set the "name" - this will be the name listed in the project list
- set "Project Path" this should be the root folder of the project

![](/readmeScreenshots/projectEdit1.png)

### Add Project Variables
a Project Variable is a variable that is accessible to any template
- click "add a Variable" button
- set a name, this will be a key to access the variable from a template
- set a type, at the moment the two option have no difference and more just a label
- set a value - the value used when the variable name is used in a template

![](/readmeScreenshots/projectEdit2.png)

### Saving Project
- click "Save" after you have finished editing

### Add Template

a Template is a collection of Variable and template Files

- on the "Home" page click "Edit Templates"
- then on the "Templates" page click "Add Template" button
- enter and name for the template
- set a "Directory Path" for the template. this will be a relative path to the Code tool user folder. 
For example a value of "\exampleTemplate" would be "C:\Users\bob\CodeTool\exampleTemplate"

![](/readmeScreenshots/templateEdit1.png)

### Add Variable
a Variable is used to inject a value into a temaple 
- click "Add Variable"
- give the Variable a name 
- select a type
- if you select a path type you can set a "Start Path" that can be a hardcoded string or use replacement key "{{name}}". this will just set the start position for the folder picker
- a Default value will just preset the field when the template is used

![](/readmeScreenshots/templateEdit2.png)

### Add Template File
a Template File is the file that is created when a template is a applied to a project

- click the "+" button right of the "Files" title, on the left hand Nav bar 
- this will create a new file called "newFile0.txt"
- click the "newFile0.txt" row
- change the path to something more meaningful (path relative to the template folder)
- set "Path Destination" (this will be relative to the selected project root, when applying a template)
- the bottom box is the file content
- enter any text and variables 
- variable names surrounded by "{{" & "}} will be replaced by the variable value (eg variable "{{name}}) when a template is applied.
- click "save"

![](/readmeScreenshots/templateEdit3.png)

### Apply Template
when Applying a template you will select a project, then a template then fill out the variable values.
Applying the template that will create the files in the project
- go back to the home page
- click a project
- select template from the left hand nav bar (if you have created only one template it will already be selected )
- fill out the variable values
- click "Apply Template"
- this will create the files list below the button in the selected project

![](/readmeScreenshots/templateApply1.png)

## Example

### project Edit

Home -> ProjectEdit

setup a new project

![](/readmeScreenshots/exsample0.png)

### template Edit General Info

Home -> (Template)template List -> (Add Template)Template Edit -> General Info

create a new Template

![](/readmeScreenshots/exsample1.png)

### template Edit - Files first File

Home -> (Template)template List -> (Add Template)Template Edit -> ViewModel.txt

add a template file

the suffix "$import" used with {{mainPackage$import}} gives "com.jaehl.demo" (replaces "\" with "." and removes first ".") when used in the demo project

![](/readmeScreenshots/exsample2.png)

### template Edit - Files second file

Home -> (Template)template List -> (Add Template)Template Edit -> Interface.txt

add a template file

![](/readmeScreenshots/exsample3.png)

### template Apply - before Variables entered

Home -> Demo -> Example

![](/readmeScreenshots/exsample4.png)

### template Apply - After Variables entered

Home -> Demo -> Example

![](/readmeScreenshots/exsample5.png)
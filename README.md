# tool-payload-creator

Tool to try to create a payload as close a possible to desired size

[![Keep a Changelog v1.1.0 badge](https://img.shields.io/badge/changelog-Keep%20a%20Changelog%20v1.1.0-%23E05735)](https://github.com/fugerit-org/tool-payload-creator/blob/master/CHANGELOG.md) 
[![license](https://img.shields.io/badge/License-Apache%20License%202.0-teal.svg)](https://opensource.org/licenses/Apache-2.0)
[![code of conduct](https://img.shields.io/badge/conduct-Contributor%20Covenant-purple.svg)](https://github.com/fugerit-org/fj-universe/blob/main/CODE_OF_CONDUCT.md)

![Java runtime version](https://img.shields.io/badge/run%20on-java%208+-%23113366.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Java build version](https://img.shields.io/badge/build%20on-java%2011+-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-3.9.0+-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)

## Quickstart

build : 

`mvn clean install -P singlepackage`

run : 

```
java -jar target/dist-tool-payload-creator-X.X.X.jar \
	--target-size-byte 102400 \
	--target-format pdf \
	--output-file target/test.pdf
```

|parameter help      |required|description                                                                                            |
|--------------------|--------|-------------------------------------------------------------------------------------------------------|
|`target-size-byte`   |`true`   |Target size in byte (the tool will try to get as close as possible, without surpassing it)             |
|`target-format`      |`true`   |Target format (currently supported formats : 'pdf' )                                                   |
|`output-file`        |`true`   |Path to output file (ex. target/test.pdf)                                                              |
|`create-base64`      |`false`  |If set, the output will be converted to base 64 and saved to the given path                            |
|`create-base64`      |`false`  |If set, the output will be converted to base 64 and saved to json file in give path, the content will be `{"content":"${base64}"}` |




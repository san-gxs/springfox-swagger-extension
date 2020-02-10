# srpingfox-swagger-extension
[![Build Status](https://travis-ci.org/uhfun/swagger-more.svg?branch=master)](https://travis-ci.org/uhfun/swagger-more) ![jdk1.8](https://img.shields.io/badge/jdk-1.8-blue.svg) 

## About

​		公司内部系统，很多服务是基于dubbox开发的，配置文件很繁琐，接口文档基于confluence进行维护，维护上比较困难，容易断更，还增加了工作量。现有的Swagger2文档
对dubbo api支持并不友好，本项目基于springboot和springfox进行开发，提供对alibba,apache dubbo的注解支持，自动生成dubbo文档。


## UI

> **支持官方的swagger ui,自定义UI待后续版本更新**
>

## Features

1. 自动生成dubbo接口文档
2. 兼容alibaba dubbo 和 apache dubbo
3. 兼容springfox swagger2的ui、提供一个符合java接口文档的新UI



## How to use

[1.0.0-SNAPSHOT Demo](https://github.com/luffytalory/srpingfox-swagger-extension/tree/master/springfox-swagger-extension-demo)


## Versions

- 1.0.0-SNAPSHOT
    1. 自动生成dubbo接口文档，同springfox 的JSON API
    2. 接口支持调试，支持泛型调用，解决接口方法多对象传参以及方法重载的问题
    3. 兼容alibaba dubbo 和 apache dubbo
    4. 兼容springfox swagger2的ui、提供一个符合java接口文档的新UI


## Copyright

```
Copyright (c) 2019 talor

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

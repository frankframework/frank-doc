# FF! Doc Library

![frank-framework-github-banner](banner.png)

See [@frankframework/ff-doc readme](./projects/ff-doc/README.md)
<!--
# Angular Library Template

A template repository for Angular libraries

![frank-framework-github-banner](banner.png)

## How to use
Find and replace all `ff-doc` with your project name, you should also rename the `/projects/PROJECT_NAME` folder and update `.eslintrc.js > parserOptions > project` property accordingly.

> [!IMPORTANT]
> Make sure to update angular to the latest version

This template repository consists of 2 angular projects which split the responsibility of developing vs publishing only what's needed:
  - The main project (angular-lib-poc in this case)
    - Lives in `<root>/src` like any other normal angular project
    - Is just a regular angular project used for testing the library project
    - Uses all the default configs & files setup in the root
    - This project won't be any part of what is published to NPM
  - The library project
    - Lives in `projects/PROJECT_NAME` and is the actual library you can put on NPM
    - Is defined as project type `library` in the `angular.json`
    - Has its own `package.json` & `tsconfig` that extends the main one
    - Has an `ng-package.json`  for ng-packagr to use

Pay attention to `/package.json`, `/angular.json` & `projects/example-lib/package.json` since they define how the development environment will be set up compared to what will be published onto NPM.

### Serve
Run `npm start` to build the library project first and then serve the testing project.

### Serve with live hot-reload
Run `npm run watch` to build the library on every change. Then in a new terminal run `npm start` to serve the testing project.

### Build
Run `npm run build` to build the library project using ng-packagr. The build artifacts will be stored in the `dist/ff-doc/` directory.

### Publishing
Run `npm publish` in the `dist/ff-doc/` directory in order to publish the library to a package registry.
-->

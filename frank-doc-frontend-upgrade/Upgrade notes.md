Get frankdoc.json from [Frank!Doc website](https://frankdoc.frankframework.org/), put it in `dist/js`

## AngularJS frontend
* Mostly built with directives
* Directives can have templates
  * The `restrict` option is typically set to: `E` for element, `A` for attribute, `C` for class, and `M` for comment.
* almost no controllers (Main controller as an app shell)
* $scope is container for scoped variables

## Angular (upgrade notes)
* Convert directives mostly to component logic for template support
* `$http.get("js/frankdoc.json")` in global app service
* Hierarchy can be managed by parent <-> child component relationship
* Manage frankdoc data in a singleton service with util functions
* Convert filters to pipes
* {{::var}} is used for one-time binding, use `changeDetection: ChangeDetectionStrategy.OnPush` in component annotation to set it to `CheckOnce` for the whole component, check if this is actually wanted
* ng-bind-html from AngularJS isn't in Angular anymore, use [innerHtml] instead
 
## Upgrade steps
* Start new angular frontend project
* put `frankdoc.json` in assets folder
* put main controller logic in app component & service (serving as a global state manager)
* solution for hashbang urls to redirect to normal routes, change `#` to `#!`: [stackoverflow](https://stackoverflow.com/a/49534503/9929992)
* convert directives with templates to components
  * first convert directives that are not loaded via angular router
  * put router loadable components in `src/app/views` folder
  * put other components in `src/app/components` folder
  * components that need app state & route info subscriptions which rely on each other's values, use [combineLatest](https://rxjs.dev/api/index/function/combineLatest) to get the result of lastest values of both subscriptions
* convert filters to pipes

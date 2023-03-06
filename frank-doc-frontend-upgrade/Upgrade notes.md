Get frankdoc.json from [Frank!Doc website](https://frankdoc.frankframework.org/), put it in `dist/js`

## AngularJS frontend
* Mostly built with directives
* Directives can have templates
  * The `restrict` option is typically set to: `E` for element, `A` for attribute, `C` for class, and `M` for comment.
* Almost no controllers (Main controller as an app shell), some directives do have a controller found in `controllers.js`
* $scope is container for scoped variables
* Has a [multi element directive](https://docs.angularjs.org/api/ng/service/$compile#-multielement-) concept

## Angular (upgrade notes)
* Convert directives mostly to component logic for template support
* `$http.get("js/frankdoc.json")` in global app service
* Hierarchy can be managed by parent <-> child component relationship
* Manage frankdoc data in a singleton service with util functions
* Convert filters to pipes
* {{::var}} is used for one-time binding, use `changeDetection: ChangeDetectionStrategy.OnPush` in component annotation to set it to `CheckOnce` for the whole component, check if this is actually wanted
* ng-bind-html from AngularJS isn't in Angular anymore, use `[innerHtml]=""` instead
* ng-attr-* has also been changed to `[attr]=""`
* convert multi element directives to a directive on `ng-template` element with encapsulated elements inside
 
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

## questions
* > Error: src/app/views/element-attributes/element-attributes.component.html:3:21 - error TS2339: Property 'nonInheritedAttributes' does not exist on type 'Element'.
  Is `nonInheritedAttributes` still a thing in the frankdoc?
  * Is going to be figured out in the future
* parent-element is almost identical to element, maybe make a container component for both?` (if recursion is possible)
  * Merge element & parent-element into one recursive component

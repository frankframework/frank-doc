Get frankdoc.json from [Frank!Doc website](https://frankdoc.frankframework.org/), put it in `dist/js`

## AngularJS frontend
* Mostly built with directives
* Directives can have templates
* almost no controllers (Main controller as an app shell)
* $scope is container for scoped variables

## Angular (upgrade notes)
* Convert directives mostly to component logic for template support
    * The `restrict` option is typically set to: `E` for element, `A` for attribute, `C` for class, and `M` for comment.
* `$http.get("js/frankdoc.json")` in global app service
* Hierarchy can be managed by parent <-> child component relationship
* Manage frankdoc data in a singleton service with util functions
* Convert filters to pipes
* {{::var}} is used for one-time binding, use `changeDetection: ChangeDetectionStrategy.OnPush` in component annotation to set it to `CheckOnce` for the whole component, check if this is actually wanted
 
## Upgrade steps
* Start new angular frontend project
* put `frankdoc.json` in assets folder
* put main controller logic in app component & service (serving as a global state manager)
* convert directives with templates to components
    * put router loadable components in `src/app/views` folder
    * put other components in `src/app/components` folder
* convert filters to pipes

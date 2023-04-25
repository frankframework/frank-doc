## Resources

- [AngularJS Guide](https://docs.angularjs.org/guide)
- [Angular Docs](https://angular.io/docs)
- [AngularJS to Angular Cheat Sheet](https://angular.io/guide/ajs-quick-reference)
- [ECMAScript feature list (ES6+)](https://github.com/daumann/ECMAScript-new-features-list/blob/master/README.md)

## AngularJS frontend

- Mostly built with directives
- Directives can have templates
  - The `restrict` option is typically set to: `E` for element, `A` for attribute, `C` for class, and `M` for comment.
- Almost no controllers (Main controller as an app shell), some directives do have a controller found in `controllers.js`
- A controller can be bound to an element with `ng-controller` directive
- $scope is container for scoped variables made per controller, scoped variables can be inherited from parent controller/scope
- Has a [multi element directive](https://docs.angularjs.org/api/ng/service/$compile#-multielement-) concept

## Angular (upgrade notes)

- Convert directives mostly to component logic for template support
- Hierarchy can be managed by parent <-> child component relationship
- Convert filters to pipes
- `{{::var}}` is used for one-time binding, use `changeDetection: ChangeDetectionStrategy.OnPush` in component annotation to set it to `CheckOnce` for the whole component, check if this is actually wanted
- `ng-bind-html` from AngularJS isn't in Angular anymore, use `[innerHtml]=""` instead
- `ng-attr-*` has also been changed to `[attr]=""`
- convert multi element directives to a directive on `ng-template` element with encapsulated elements inside

## Upgrade steps (Frank!Doc)

- Start new angular frontend project
- all back-end generated assets (frankdoc.json, frankdoc.xsd) should be put in the same position as in the old frontend
  - created folders to represent `/js` and `/xml/xsd`
  - add both folders to angular.json under `assets` (`src/js` & `src/xml/xsd`)
- put main controller logic in app component & service (serving as a global state manager)
  - state management & util functions should be put in the app service
  - main app state should be managed with a behavior subject & exposed observable
    ```ts
    private frankDocStateSource = new BehaviorSubject<AppState>(this.emptyState);
    /*(public)*/ frankDoc$ = this.frankDocStateSource.asObservable();
    ```
- solution for hashbang urls to redirect to normal routes, change `#` to `#!`: [stackoverflow](https://stackoverflow.com/a/49534503/9929992)
- convert directives with templates to components
  - first convert directives that are not loaded via angular router
  - put router loadable components in `src/app/views` folder
  - put other components in `src/app/components` folder
  - components that need app state & route info subscriptions which rely on each other's values, use [combineLatest](https://rxjs.dev/api/index/function/combineLatest) to get the result of lastest values of both subscriptions
- convert filters to pipes
  - $scope variables should be gotten through extra parameters or through app service
- Add global styling
  - Install bootstrap & font-awesome
  - Add bootstrap & font-awesome to angular.json under `projects.<project name>.architect.build.options.assets` (and in the `assets` array for the test architecture)
  - Create a file for the old styles called `old.scss` and import it in `styles.scss`
  - Tweak the old styles to work with angular's DOM rendering
  - If a routed component needs to have a class assigned to it, use `host: { class: '<class name(s)>' }` in the component annotation
- To have the angular-cli build it in `/dist` instead of `/dist/<project name>`, add `"outputPath": "dist"` to angular.json under `projects.<project name>.architect.build.options`

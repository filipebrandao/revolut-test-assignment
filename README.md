# revolut-test-assignment
This project is my solution to the assignment proposed by Revolut as part of the hiring process for the Android Developer position.

## Requirements
A set of requirements for this assignment was built based in the provided mockup and video demo and also, since the assignment definition was vague and open ended in many ways (specially input validation and error edge cases), it was useful to get some assumptions by testing the rates list screen of the official Revolut app. 

#### Functional requirements:
- A list of currencies is shown with a logo, short name, long name and a value text field.
- The first currency in the list has an editable text field which is the currency value.
- Updating the currency value will cause all the other currencies' values to be calculated based on the current currency rates.
- Currency rates are updated every second using the remote api which causes the values to be recalculated and updated in the UI.
- Currency logos are fetched from XE's website and consider a placeholder in case they are not found.
- Tapping a currency list item moves it to the top making that currency the active one (which means that all other currencies values will be calculated based on this new one).
- Currency value shows hint text "0" when empty.

#### Input validation:
- Currency value input is restricted to numeric digits.
- Currency value input allows for a maximum of 9 digits.
- Currency value input allows for a maximum of 2 decimal digits.
- Leading zeroes are not allowed to be inputted and shall be stripped out.

#### Errors:
- If network connection is lost, a warning is shown to alert the user.
- If the rates fail to be fetched from the network on the first try, an error screen is shown with a retry button.

## Quality
Quality-oriented practices included:
- Static analysis through the usage of linters : detekt, ktlint and the Android Studio's linter. 
- The code has automated tests, most of the defined requirements have matching UI Tests built with the Espresso framework.
- Tested in an emulator running API 23 and device running API 29.

## Architecture
On top of a set of requirements that felt complete enough and the quality concerns mentioned before, to consider the app as production-ready the architecture of the app was planned with requirements such as maintainability, testability and scalability in mind:
 - The code organized in several modules (app, api, domain, common) to respect the single-responsibility principle and allow for faster build times.
 - The code architecture was inspired by SOLID principles and the well-known Clean Architecture.
- Koin was used to build a dependency injection environment.

The UI is mostly reactive while making sure that no needless work is being executed in the UI thread:
 - The presentation layer of the app uses the MVVM pattern, leveraging on Android LiveData and ViewModels.
 - Avoiding work in the UI thread, asynchronous operations are exposed through a RX interface: the UseCases and the Network code rely on RX (using Retrofit and its RX adapters).

## Screenshots
| ![1](../master/screenshots/1.png) | ![2](../master/screenshots/2.png) | ![3](../master/screenshots/3.png) | ![4](../master/screenshots/4.png)
|:---:|:---:|:---:|:---:|
| Initial state | After entering a value | Warning when the connection drops | Error screen when loading the rates for the first time

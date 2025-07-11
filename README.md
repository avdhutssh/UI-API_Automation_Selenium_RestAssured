<!--
    #/**
    # * @author Avdhut Shirgaonkar
    # * Email: avdhut.ssh@gmail.com
    # * LinkedIn: https://www.linkedin.com/in/avdhut-shirgaonkar-811243136/
    # */
    #/***************************************************/
-->

# UI & API Test Automation Framework Using Selenium & RestAssured

## Table of Contents

- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Test Execution](#test-execution)
- [Test Suites](#test-suites)
- [Data-Driven Testing](#data-driven-testing)
- [Framework Features](#framework-features)
- [Reporting](#reporting)
- [CI/CD Integration](#cicd-integration)
- [Contacts](#contacts)

## Introduction

This repository contains a comprehensive test automation framework for the **E-commerce** application, combining both **UI (Selenium)** and **API (REST Assured)** testing capabilities. The framework is built using Java and Maven, designed to automate end-to-end test scenarios for the e-commerce platform

The framework follows industry best practices such as:

- **Page Object Model (POM)** for UI test maintainability
- **Modular API architecture** with REST Assured
- **Object-oriented design** principles (SOLID)
- **Factory and Builder patterns** for request/response specifications
- **Data-driven testing** with CSV and JSON support
- **Parallel test execution** using TestNG
- **Comprehensive reporting** with Allure Reports
- **Enhanced logging** with Log4J2
- **CI/CD ready** with multiple test suites
- **Retry mechanism** for handling flaky tests
- **Soft assertions** for better test coverage

## Prerequisites

Before you start, ensure you have the following installed on your machine:

- **Java Development Kit (JDK)**: Version 17 or later
- **Maven**: To manage project dependencies
- **Git**: To clone the repository
- **Chrome Browser**: Primary browser for UI testing (or configure other browsers)
- **An IDE**: (such as IntelliJ IDEA or Eclipse) with TestNG, Lombok plugins installed

For CI/CD:

- **Jenkins**: For automated build and test execution
- **GitHub Actions**: Integrated for CI pipeline

## Project Structure

This framework follows a hybrid approach combining UI and API testing with modular architecture. The structure is designed for maintainability, scalability, and follows SOLID principles:

```plaintext
UI-API_Automation_Selenium_RestAssured/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ecom/
│   │   │           └── app/
│   │   │               ├── constants/                    # API endpoints and HTTP status codes
│   │   │               │   ├── Endpoints.java           # REST API endpoint constants
│   │   │               │   └── StatusCode.java          # HTTP status code constants
│   │   │               ├── PageObjects/                 # Page Object Model classes
│   │   │               │   ├── CartPage.java            # Shopping cart page interactions
│   │   │               │   ├── CheckoutPage.java        # Checkout process page
│   │   │               │   ├── LoginPage.java           # Login page interactions
│   │   │               │   ├── OrderConfirmationPage.java # Order confirmation handling
│   │   │               │   └── ProductPage.java         # Product listing and details
│   │   │               ├── pojo/                        # Plain Old Java Objects for API data models
│   │   │               │   ├── auth/                    # Authentication related models
│   │   │               │   │   ├── LoginRequest.java    # Login request payload
│   │   │               │   │   └── LoginResponse.java   # Login response model
│   │   │               │   ├── common/                  # Common API models
│   │   │               │   │   └── ErrorResponse.java   # Error response structure
│   │   │               │   ├── order/                   # Order management models
│   │   │               │   │   ├── OrderDetails.java    # Order details structure
│   │   │               │   │   ├── OrderHistoryResponse.java # Order history response
│   │   │               │   │   ├── OrderRequest.java    # Order creation request
│   │   │               │   │   └── OrderResponse.java   # Order creation response
│   │   │               │   └── product/                 # Product related models
│   │   │               │       ├── Product.java         # Product information model
│   │   │               │       └── ProductsResponse.java # Products listing response
│   │   │               └── Utilities/                   # Core utility classes
│   │   │                   ├── BasePage.java            # Base page class with common wait functions
│   │   │                   ├── BrowserDriverFactory.java # Multi-browser driver management
│   │   │                   ├── ConfigurationUtils.java  # Configuration file handling
│   │   │                   ├── CsvDataProviders.java    # CSV data provider utilities
│   │   │                   ├── ElementUtils.java        # Web element interaction utilities
│   │   │                   ├── JSONDataReader.java      # JSON test data reader
│   │   │                   └── ScreenshotUtils.java     # Screenshot capture utilities
│   │   └── resources/
│   │       ├── config.properties                        # Application configuration
│   │       └── log4J2.xml                              # Logging configuration
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── ecom/
│       │           └── app/
│       │               ├── BaseComponents/              # Base test infrastructure
│       │               │   ├── BaseTest.java           # Base test class with setup/teardown
│       │               │   ├── RetryTestListener.java  # Test retry mechanism
│       │               │   └── TestListener.java       # Custom TestNG listener
│       │               ├── generic/                    # API testing core components
│       │               │   ├── RequestFactory.java     # Factory for API request creation
│       │               │   └── RestClient.java         # REST client wrapper for RestAssured
│       │               ├── specs/                      # Request/Response specifications
│       │               │   ├── RequestSpecificationBuilder.java  # Request spec builder
│       │               │   └── ResponseSpecificationBuilder.java # Response spec builder
│       │               ├── Tests/                      # Test case classes
│       │               │   ├── _01_Login_Tests.java    # Login functionality tests
│       │               │   ├── _02_Product_Tests.java  # Product related tests
│       │               │   ├── _03_Cart_Tests.java     # Shopping cart tests
│       │               │   └── _04_E2E_Tests.java      # End-to-end scenario tests
│       │               ├── utils/                      # Test utilities
│       │               │   ├── AllureReportUtils.java  # Allure reporting utilities
│       │               │   ├── AllureRestAssuredFilter.java # API request/response capture
│       │               │   └── AssertionUtils.java     # Custom assertion utilities
│       │               └── standAloneScripts.java      # Independent test scripts
│       └── resources/
│           ├── allure.properties                       # Allure reporting configuration
│           ├── TestData/                              # Test data files
│           │   ├── _01_Login_Tests/                   # Login test specific data
│           │   │   └── test_03_API_verifyMultipleInvalidLoginAttempts.csv
│           │   ├── loginTestData.json                 # JSON login test data
│           │   ├── standAloneScripts/                 # Standalone script data
│           │   │   └── test_03_API_verifyMultipleInvalidLoginAttempts.csv
│           │   └── testdata.json                      # General test data
│           └── TestSuites/                            # TestNG suite configurations
│               ├── SmokeSuite.xml                     # Smoke test suite
│               ├── RegressionSuite.xml                # Regression test suite
│               └── WholeSuite.xml                     # Complete test suite
├── screenshots/                                        # Runtime screenshot storage
├── target/                                            # Maven build output
├── pom.xml                                            # Maven project configuration
```

## Technologies Used

- **Java** - Programming language
- **Selenium** - Web automation
- **TestNG** - Test framework
- **REST Assured** - API testing
- **Allure** - Reporting
- **Maven** - Build tool
- **Jackson** - JSON processing
- **Log4J2** - Logging framework

## Getting Started

1. **Clone the repository:**

```bash
git clone https://github.com/avdhutssh/UI-API_Automation_Selenium_RestAssured.git
```

2. **Navigate to the project directory:**

```bash
cd UI-API_Automation_Selenium_RestAssured
```

3. **Install dependencies:**

```bash
mvn clean install
```

This will download all required dependencies including Selenium, REST Assured, TestNG, Allure Reports, and Log4J2.

## Test Execution

### Run All Tests (Default - Smoke Suite)

```bash
mvn clean test
```

### Run Specific Test Suite

```bash
# Smoke Tests (7 tests)
mvn clean test -Psmoke allure:serve

# Regression Tests (14 tests)  
mvn clean test -Pregression allure:serve

# All Tests (21 tests)
mvn clean test -Pall-tests allure:serve
```

### Run Tests with Browser Selection

```bash
# Chrome browser (default)
mvn clean test -Dbrowser=chrome

# Headless mode
mvn clean test -Dheadless=true
```

## Test Suites

The framework includes multiple pre-configured test suites:

### 1. Smoke Suite (SmokeSuite.xml) - 7 Tests
**Critical functionality validation:**
- User login/logout validation
- Invalid login handling
- Product search functionality
- Basic cart operations
- Cart clearing functionality
- Complete E2E order flow
- API order history verification

### 2. Regression Suite (RegressionSuite.xml) - 14 Tests
**Comprehensive feature testing:**
- Extended login scenarios with CSV data
- Multiple product interactions
- Advanced cart management
- Detailed UI validations
- API integration testing
- Error handling scenarios

### 3. Whole Suite (WholeSuite.xml) - 21 Tests
**Complete test coverage:**
- All smoke tests
- All regression tests
- Full UI and API integration
- End-to-end workflows

## Data-Driven Testing

The framework supports multiple data-driven testing approaches:

### CSV Data Providers

Example usage in test methods:

```java
@Test(dataProvider = "csvFileReader", dataProviderClass = CsvDataProviders.class)
public void test_03_API_verifyMultipleInvalidLoginAttempts(Map<String, String> testData) {
    String email = testData.get("email");
    String password = testData.get("password");
    String expectedMessage = testData.get("expectedMessage");
    
    // Test implementation
    LoginResponse response = getRequestFactory().login(email, password);
    AssertionUtils.assertEquals(response.getMessage(), expectedMessage);
}
```

### JSON Data Support

```java
// Reading JSON test data
JSONDataReader jsonReader = new JSONDataReader();
Map<String, Object> testData = jsonReader.getTestData("loginTestData.json");
```

### Dynamic Test Data

```java
// Using configuration-driven test data
String productName = ConfigurationUtils.getProperty("productName");
String countryName = ConfigurationUtils.getProperty("countryName");
```

## Framework Features

### 1. **Enhanced REST Assured Architecture**
- **RestClient**: Generic HTTP operations with logging and Allure integration
- **RequestFactory**: E-commerce specific API operations
- **Specification Builders**: Reusable request/response specifications
- **AllureRestAssuredFilter**: Automatic API request/response capture

### 2. **Robust UI Automation**
- **Page Object Model**: Clean separation of test logic and UI elements
- **ElementUtils**: Common web element operations with explicit waits
- **BasePage**: Shared functionality across all page objects
- **Multi-browser support**: Chrome, Firefox, Edge with configurable options

### 3. **Advanced Test Management**
- **BaseTest**: Comprehensive setup with UI and API components
- **TestListener**: Custom TestNG listener with screenshot capture
- **RetryListener**: Automatic retry mechanism for flaky tests
- **Soft Assertions**: Continue execution after assertion failures

### 4. **Intelligent Reporting**
- **AllureReportUtils**: Enhanced step logging and test data attachment
- **Screenshot Integration**: Automatic capture on failures
- **API Documentation**: Request/Response logging with formatting
- **Test Categorization**: Epic, Feature, Story annotations

### 5. **Data Management**
- **Multiple Data Sources**: CSV, JSON, Properties files
- **Configuration Management**: Environment-specific settings
- **Test Data Isolation**: Separate data files per test class
- **Dynamic Data Generation**: Runtime test data creation

## Reporting

### Allure Reports (Primary)

Generate and view comprehensive Allure reports:

```bash
# Clean previous results and generate fresh report
mvn clean test allure:serve

# Generate report without serving
mvn clean test allure:report
```

**Allure Report Features:**
- Test execution timeline
- Step-by-step test breakdown
- Request/Response details for API tests
- Screenshots for UI test failures
- Test categorization with @Epic, @Feature, @Story
- Retry information and failure analysis
- Environment information and test data
- Trends and historical data

![Allure Report](./misc/allure_report.png)

### TestNG Reports (Secondary)

Basic HTML reports are generated automatically:
- Location: `target/surefire-reports/index.html`
- Includes test results, execution time, and basic statistics

## CI/CD Integration

### Jenkins Integration

**Setup Steps:**
1. Install required plugins: Maven, TestNG, Allure, HTML Publisher
2. Create Maven project in Jenkins
3. Configure SCM with your repository
4. Add build steps:

```bash
# Clean and run tests
clean test -Psmoke

# For parameterized builds
clean test -P${PROFILE} -Dbrowser=${BROWSER}
```

**Post-build Actions:**
- Publish TestNG Results: `target/surefire-reports/testng-results.xml`
- Allure Report: `target/allure-results`
- Archive Screenshots: `screenshots/`

Happy Learning!

Avdhut Shirgaonkar

# eCommerce Platform

## Overview
This eCommerce platform is designed to facilitate online shopping by providing functionalities for customers, sellers, and administrators. The application is built using Java and Spring Boot, ensuring a robust and scalable architecture.

## Features
- **User Management**: Registration, login, and profile management for customers and sellers.
- **Product Management**: Sellers can add, update, and manage their products.
- **Order Management**: Customers can place orders, view order history, and manage their shopping cart.
- **Payment Processing**: Secure payment handling for customer orders.

## Project Structure
```
ecommerce-platform
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com
│   │   │   │   └── ecommerce
│   │   │   │       ├── Application.java
│   │   │   │       ├── controllers
│   │   │   │       │   ├── AdminController.java
│   │   │   │       │   ├── CustomerController.java
│   │   │   │       │   └── SellerController.java
│   │   │   │       ├── models
│   │   │   │       │   ├── User.java
│   │   │   │       │   ├── Product.java
│   │   │   │       │   ├── Order.java
│   │   │   │       │   ├── Cart.java
│   │   │   │       │   └── Payment.java
│   │   │   │       ├── services
│   │   │   │       │   ├── UserService.java
│   │   │   │       │   ├── ProductService.java
│   │   │   │       │   ├── OrderService.java
│   │   │   │       │   └── PaymentService.java
│   │   │   │       ├── repositories
│   │   │   │       │   ├── UserRepository.java
│   │   │   │       │   ├── ProductRepository.java
│   │   │   │       │   └── OrderRepository.java
│   │   │   │       └── utils
│   │   │   │           └── Constants.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── com
│               └── ecommerce
│                   ├── controllers
│                   ├── services
│                   └── models
├── pom.xml
└── README.md
```

## Setup Instructions
1. **Clone the Repository**: 
   ```
   git clone <repository-url>
   cd ecommerce-platform
   ```

2. **Build the Project**: 
   ```
   mvn clean install
   ```

3. **Run the Application**: 
   ```
   mvn spring-boot:run
   ```

4. **Access the Application**: Open your web browser and navigate to `http://localhost:8080`.

## Usage Guidelines
- **Admin**: Use the AdminController to manage users and products.
- **Customer**: Use the CustomerController to browse products, manage the cart, and place orders.
- **Seller**: Use the SellerController to add products and manage inventory.

## Contributing
Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.

## License
This project is licensed under the MIT License.
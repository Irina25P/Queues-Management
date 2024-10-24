# Description
- This project is an Orders Management System for a warehouse, following a layered architecture and utilizing a relational database.
- It allows for the efficient management of clients, products, and orders through a Java-based application with a graphical user interface (GUI).
- The project supports CRUD operations for managing clients and products, processing orders, and generating invoices using Java records for immutable objects.

# Features
- Client Management: Add, edit, and delete client records in the database.
- Product Management: Manage product inventory, including stock updates.
- Order Processing: Place orders, check product availability, and update stock levels automatically.
- Invoice Generation: Automatically generates immutable invoices (Bill) for each order.
- Database Interaction: Uses JDBC for interacting with a MySQL database, leveraging reflection for dynamic query generation.
- Layered Architecture: Separates the application into Data Access Object (DAO), Business Logic (BLL), and Presentation layers for better organization and maintainability.
- GUI: User-friendly Java Swing interface for managing clients, products, orders, and viewing invoices.

# Project Structure
- Main Class (Main): Entry point of the application, located in org.example.
- Model:
  - Client: Represents a client entity with attributes like name, address, and email.
  - Product: Represents a product entity, including stock and price.
  - Order: Represents an order, linking clients to the products they purchase.
  - Bill: An immutable class representing invoices.
- Business Logic (BLL):
  - ClientBll, ProductBll, OrderBll, BillBll: Handle business rules and validations for each entity.
  - Validators: Ensure that data like emails, prices, and stock levels are correctly validated.
- Data Access Layer (DAO):
  - ClientDao, ProductDao, OrderDao, BillDao: Manage CRUD operations for each entity using JDBC.
  - GenericDao: Provides generic methods for common database operations.
  - ConnectionFactory: Manages database connections.
- Presentation Layer:
  - ClientView, ProductView, OrderView, BillView: Swing-based interfaces for interacting with each module.
  - Controllers: Manage user interactions and connect the views with the business logic.

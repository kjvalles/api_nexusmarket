# Domain Model

## Introduction

The Domain Model represents the core business entities of the NexusMarket e-commerce platform. These entities encapsulate the business rules, data, relationships, and lifecycle concepts described in the system specification.

The model follows Object-Oriented Design and Domain-Driven Design (DDD) principles. Inheritance is used to represent genuine domain specialization, while explicit object relationships are preferred over generic identifier fields for data stored in the same database. Because NexusMarket uses a hybrid persistence architecture (MongoDB for catalog/users, MySQL for transactions/reports), cross-database relationships are handled via explicit ID references.

The model distinguishes between:

* **Users**, which represent identifiable people or companies and their role within the marketplace.
* **Catalog & Inventory**, which represent products, their variants, and the warehouses where they are stored.
* **Transact & Fulfillment**, which represent the commercial lifecycle, including shopping carts, orders, payments, invoicing, shipments, and returns.
* **Ledger & Stock**, which provide an immutable historical record of inventory movements and a consolidated stock view.

---

# Domain Class Hierarchy

```text
User (Abstract)
├── Buyer
└── Seller

Warehouse

Product

ShoppingCart

Order

InventoryMovement

ProductStock

PaymentTransaction

Invoice

Shipment

Return

Refund
```

---

# Domain Relationships

```text
User
   │
   ├── Buyer
   │      ├── owns ───────────────> ShoppingCart
   │      └── places ─────────────> Order
   │
   └── Seller
          ├── manages ────────────> Warehouse
          └── lists ──────────────> Product

Product
   │
   ├── stored in ─────────────────> Warehouse
   ├── tracked via ───────────────> InventoryMovement
   └── balanced via ──────────────> ProductStock

Order
   │
   ├── paid via ──────────────────> PaymentTransaction
   │                                   │
   │                                   └── invoiced via ──────> Invoice
   │
   ├── fulfilled via ─────────────> Shipment
   │
   └── returned via ──────────────> Return
                                       │
                                       └── reimbursed via ────> Refund
```

---

# Entities

---

# User (Abstract)

## Description

Represents any identifiable user within the NexusMarket system.

This abstract class centralizes the common identity, contact information, and authentication credentials shared by all actors in the platform.

The role assigned to a user represents what that user means within the system and determines their permissions.

This class cannot be instantiated directly.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | String | Unique identifier of the user (MongoDB). |
| identityDocument | String | National identification number or tax ID. |
| fullName | String | Full name of the individual or legal representative. |
| email | String | Primary registered email address used for login. |
| password | String | Secure password hash stored by the system (BCrypt). |
| role | UserRole | Business role that defines permissions. |
| status | UserStatus | Current operational status of the user's system access. |
| createdAt | LocalDateTime | Timestamp of account creation. |
| updatedAt | LocalDateTime | Timestamp of the last account update. |

## Relationships

* A `User` is specialized as a `Buyer` or a `Seller`.

---

# Buyer

## Description

Represents a customer who purchases products on the marketplace.

Buyers manage shipping addresses, maintain a virtual balance for internal payments or refunds, and generate orders.

## Inherits From

`User`

## Attributes

| Attribute | Type | Description |
|---|---|---|
| primaryAddress | Address | Default shipping and billing address. |
| additionalAddresses | List\<Address\> | Other saved addresses. |
| commercialStatus | BuyerCommercialStatus | Determines the buyer's ability to make purchases. |
| virtualBalance | BigDecimal | Funds available for internal purchases or refunds. |
| completedOrdersCount | int | Number of successfully delivered orders. |
| rating | double | Average rating given by sellers (0.0 - 5.0). |

---

# Seller

## Description

Represents a merchant or vendor who lists products and manages warehouses.

## Inherits From

`User`

## Attributes

| Attribute | Type | Description |
|---|---|---|
| businessName | String | Commercial name of the seller's store. |
| taxId | String | Tax identification number (NIT). |
| warehouseIds | List\<String\> | References to `Warehouse` entities managed by the seller. |
| sellerStatus | SellerStatus | Operational status in the marketplace. |
| businessDescription | String | Description of the seller's business. |
| rating | double | Average rating given by buyers (0.0 - 5.0). |
| incorporationDate | LocalDateTime | Date the business was registered on the platform. |
| activeProductsCount | int | Number of currently published products. |

---

# Warehouse

## Description

Represents a physical location where products are stored.

Warehouses can belong directly to NexusMarket (Marketplace) or to a private Seller.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | String | Unique identifier (MongoDB). |
| name | String | Name of the warehouse. |
| address | Address | Physical location of the warehouse. |
| vendorId | String | Reference to the `Seller`. Null if the warehouse is of type MARKETPLACE. |
| type | WarehouseType | Indicates if managed by the marketplace or a seller. |
| status | WarehouseStatus | Operational status (Active, Closed). |
| description | String | Additional details about the facility. |
| maxCapacity | int | Maximum item capacity. |

---

# Product

## Description

Represents a sellable item in the catalog. 

Products can have multiple variants (colors, sizes) and can be physical or digital. The product lifecycle determines its visibility in the catalog.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | String | Unique identifier (MongoDB). |
| vendorId | String | Reference to the `Seller` owning the product. |
| name | String | Product name. |
| description | String | Product details. |
| category | String | Product classification category. |
| basePrice | BigDecimal | Base price in COP. Variant prices are calculated over this base. |
| type | ProductType | Determines logistical rules (Physical vs Digital). |
| status | ProductStatus | Visibility status in the catalog. |
| variants | List\<ProductVariant\> | Available options with specific SKUs. |
| imageUrls | List\<String\> | Media gallery for the product. |
| averageRating | double | Average customer rating (0.0 - 5.0). |
| totalSales | int | Total number of units sold. |

---

# ShoppingCart

## Description

Represents a buyer's temporary selection of products before checkout.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | String | Unique identifier (MongoDB). |
| buyerId | String | Reference to the `Buyer` who owns the cart. |
| items | List\<CartItem\> | List of selected product variants and quantities. |

---

# Order

## Description

Represents a confirmed purchase request by a buyer. It acts as the aggregate root for the transactional flow (MySQL).

The order lifecycle is strictly unidirectional. An order captures a snapshot of product details and prices at the moment of purchase to prevent historical data alteration.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | Long | Unique identifier (MySQL). |
| buyerId | String | Cross-database reference to the `Buyer`. |
| status | OrderStatus | Current lifecycle state of the order. |
| subtotal | BigDecimal | Sum of all order lines. |
| taxes | BigDecimal | Calculated tax amount. |
| discounts | BigDecimal | Applied discounts. |
| totalAmount | BigDecimal | Final amount to be paid. |
| additionalNotes | String | Special requests from the buyer. |
| orderLines | List\<OrderLine\> | Snapshot of purchased products and prices. |

## Examples of Associated Flows
* Creates a `PaymentTransaction`.
* Triggers `InventoryMovement` for reservations.
* Generates a `Shipment` if products are physical.

---

# InventoryMovement

## Description

Represents a single, immutable transaction affecting inventory levels, acting as a ledger entry.

Movements can represent sales, manual entries, reservations, or losses.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | Long | Unique identifier (MySQL). |
| productId | String | Cross-database reference to `Product`. |
| warehouseId | String | Cross-database reference to `Warehouse`. |
| variantId | String | Reference to the specific `ProductVariant`. |
| quantity | int | Number of units involved (positive for entry, negative for exit). |
| movementType | InventoryMovementType | The nature of the inventory change. |
| referenceOrderId | Long | Reference to an `Order`. Null for manual adjustments. |
| registeredByUserId | String | User who recorded the movement. |

---

# ProductStock

## Description

Represents the consolidated current balance of inventory for a specific product and variant in a specific warehouse.

This entity prevents the system from having to recalculate the entire ledger of `InventoryMovement` on every query. Available quantity can never be negative.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | Long | Unique identifier (MySQL). |
| productId | String | Cross-database reference to `Product`. |
| warehouseId | String | Cross-database reference to `Warehouse`. |
| variantId | String | Reference to the specific `ProductVariant`. |
| availableQuantity | int | Total physical units in the warehouse, including reserved. |
| reservedQuantity | int | Units blocked by confirmed orders not yet dispatched. |
| sellableQuantity | int | Available quantity minus reserved quantity. |

---

# PaymentTransaction

## Description

Represents the financial transaction associated with an order. 

NexusMarket validates all payments internally without consuming external gateways, simulating credit cards and transfers, or using the buyer's virtual balance.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | Long | Unique identifier (MySQL). |
| orderId | Long | Reference to the associated `Order`. |
| amount | BigDecimal | Transaction amount in COP. |
| paymentMethod | PaymentMethod | Internal method used for payment. |
| status | PaymentStatus | Current validation status. |
| authorizationCode | String | Internally generated authorization mock code. |
| paymentReference | String | Buyer-visible reference number. |
| validationResult | String | Internal validation details. |

---

# Invoice

## Description

Represents the commercial tax invoice generated upon successful payment.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | Long | Unique identifier (MySQL). |
| orderId | Long | Reference to the `Order`. |
| paymentTransactionId | Long | Reference to the approved `PaymentTransaction`. |
| invoiceNumber | String | Sequential invoice number (e.g., FAC-2025-000001). |
| buyerFiscalData | FiscalData | Tax information of the buyer. |
| taxBreakdown | TaxBreakdown | Detailed tax and VAT breakdown. |
| status | InvoiceStatus | Current invoice state (Issued, Voided). |

---

# Shipment

## Description

Represents the logistical process of delivering physical products to a buyer.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | Long | Unique identifier (MySQL). |
| orderId | Long | Reference to the `Order`. |
| deliveryAddress | DeliveryAddress | Full shipping destination details. |
| sourceWarehouseId | String | Cross-database reference to the dispatching `Warehouse`. |
| status | ShipmentStatus | Current logistical state. |
| estimatedDeliveryDate | LocalDate | Expected arrival date. |
| internalTrackingNumber | String | Generated tracking code. |
| logisticsOperatorId | String | Reference to the user managing the logistics. |

---

# Return

## Description

Represents a buyer's request to return a physical product.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | Long | Unique identifier (MySQL). |
| orderId | Long | Reference to the `Order`. |
| productId | String | Reference to the returned `Product`. |
| variantId | String | Reference to the returned `ProductVariant`. |
| quantity | int | Units being returned. |
| reason | String | Explanation provided by the buyer. |
| status | ReturnStatus | Inspection and approval state. |
| refundId | Long | Reference to a `Refund` if the return is approved. |

---

# Refund

## Description

Represents a financial reimbursement to a buyer, originating from an order cancellation or an approved return.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| id | Long | Unique identifier (MySQL). |
| paymentTransactionId | Long | Reference to the original `PaymentTransaction`. |
| returnId | Long | Reference to the `Return`. Null if it's a direct cancellation. |
| refundedAmount | BigDecimal | Amount being returned. |
| status | RefundStatus | Financial execution state. |
| paymentMethod | PaymentMethod | Method used for the reimbursement. |

---

# Domain Lifecycle Relationship

The transactional lifecycle follows a strict sequence of events:

```text
Order Created
      │
      ▼
PaymentTransaction (PENDING -> APPROVED)
      │
      ├── Generates Invoice
      │
      ├── Triggers InventoryMovement (PURCHASE_RESERVATION)
      │
      └── Generates Shipment (if Physical)
            │
            ▼
        Shipment (PENDING_PACKING -> IN_TRANSIT -> DELIVERED)
            │
            ▼
        Order (COMPLETED)
```

In the event of a cancellation or return:

```text
Order Cancelled / Return Approved
      │
      ▼
Refund (PENDING -> APPROVED -> EXECUTED)
      │
      └── Triggers InventoryMovement (RESERVATION_RELEASE or RETURN_ENTRY)
```

---

# Domain Value Objects

## Introduction

Value Objects represent immutable concepts within the NexusMarket domain.

Unlike entities, they do not have their own identity. They are defined by their values and encapsulate controlled business concepts.

Value Objects prevent primitive values and scattered string literals from being used throughout the domain, and they map structured sub-documents in MongoDB or JSON structures in MySQL.

---

# Value Object Hierarchy

```text
DomainCatalog (Abstract)
├── BuyerCommercialStatus
├── InventoryMovementType
├── InvoiceStatus
├── OrderStatus
├── PaymentMethod
├── PaymentStatus
├── ProductStatus
├── ProductType
├── RefundStatus
├── ReturnStatus
├── SellerStatus
├── ShipmentStatus
├── UserStatus
├── WarehouseStatus
└── WarehouseType

Complex Value Objects
├── Address
├── CartItem
├── DeliveryAddress
├── FiscalData
├── OrderLine
├── ProductVariant
└── TaxBreakdown
```

---

# DomainCatalog (Abstract)

## Description

Represents a generic business catalog used throughout the domain.

All controlled business values that require a business code, display name, and description inherit from this abstraction.

## Attributes

| Attribute | Type | Description |
|---|---|---|
| code | String | Unique business identifier. |
| name | String | Human-readable name displayed within the application. |
| description | String | Business definition of the catalog value. |

## Characteristics
* Immutable.
* Equality is based on class type and code.
* Catalog values must be represented as static constants in their respective subclasses.

---

# Complex Value Objects

## Address
Represents a physical address for buyers or warehouses. Includes `street`, `city`, `state`, `postalCode`, `country`, and `additionalInstructions`.

## DeliveryAddress
Extends standard address information with logistical specifics, including `recipientName` and `recipientPhone`.

## ProductVariant
Represents specific configurations of a product (e.g., color, size). Includes `sku`, `additionalPrice`, `imageUrl`, and an `active` flag. Embedded in MongoDB Products.

## CartItem & OrderLine
Represent snapshots of selected products. They store `productName`, `variantSku`, `unitPrice`, and `quantity` at the exact moment of selection to isolate them from future catalog changes.

## FiscalData
Contains tax information required for the DIAN (Colombian tax authority), including `documentType`, `documentNumber`, and `fiscalAddress`.

## TaxBreakdown
Reflects the tax structure of a purchase, holding the `subtotal`, `taxableBase`, `vatPercentage`, `vatAmount`, `discounts`, and final `total`.

---

# Primitive Enumerations

The following concepts are simple enumerations because they represent fixed technical or security values without requiring business catalog metadata.

---

## UserRole

### Description
Represents the security roles available in the NexusMarket system, used for Spring Security integration.

### Values
* **BUYER**: Searches products, manages carts, and confirms orders.
* **SELLER**: Manages warehouses and product listings.
* **LOGISTICS_OPERATOR**: Updates shipment statuses.
* **ADMIN**: Approves sellers and manages platform settings.
* **SUPERVISOR**: Handles overrides and platform-wide monitoring.

---

# Domain Design Rules

## Hybrid Persistence
* MongoDB Entities (`User`, `Warehouse`, `Product`, `ShoppingCart`) use `String id`.
* MySQL Entities (`Order`, `InventoryMovement`, `ProductStock`, `PaymentTransaction`, `Invoice`, `Shipment`, `Return`, `Refund`) use `Long id`.
* Relationships across databases must ONLY use ID fields (e.g., `Order.buyerId`). Direct object referencing across DBs is prohibited.

## Immutability and Snapshots
* `OrderLine` and `CartItem` must capture the name, SKU, and price of the product at the moment of creation. Changes to `Product` must not alter historical orders.
* `InventoryMovement` acts as an append-only ledger. Records cannot be updated or deleted.

## Status Management
* Statuses are implemented via `DomainCatalog` instead of simple Enums to ensure robust documentation, explicit descriptions, and future extensibility.
* A change in state (e.g., Payment `APPROVED`) triggers side-effects (e.g., Invoice generation, Shipment creation) but the entities remain decoupled.

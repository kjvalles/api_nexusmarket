# Domain Value Objects

## Introduction

Value Objects represent immutable concepts within the NexusMarket e-commerce domain.

Unlike Entities, Value Objects do not have their own identity (no database ID is exposed or meaningful at the domain level). They are defined entirely by their values and are used to encapsulate controlled business concepts, improve domain expressiveness, and prevent the use of primitive values or scattered string literals throughout the application.

The NexusMarket domain uses Value Objects for two primary purposes:
1. **Business Catalogs:** Statuses, payment methods, movement types, etc. All inherit from `DomainCatalog`.
2. **Complex Structures:** Snapshots of data (e.g., `OrderLine`, `CartItem`) or embedded properties (e.g., `Address`, `FiscalData`) that are stored as nested documents in MongoDB or JSON/denormalized columns in MySQL.

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

Represents a generic business catalog used throughout the NexusMarket domain.

`DomainCatalog` provides a consistent structure for controlled business values that require a code, human-readable name, and business description.

This class cannot be instantiated directly.

## Attributes

| Attribute   | Type   | Description                                           |
| ----------- | ------ | ----------------------------------------------------- |
| code        | String | Unique business identifier of the catalog value.      |
| name        | String | Human-readable name displayed within the application. |
| description | String | Business definition of the catalog value.             |

## Characteristics

* Immutable.
* Equality is determined by class type and `code` rather than object identity.
* Catalog values are controlled by the domain as static constants.
* Catalog values must not be represented by arbitrary strings throughout the application.
* Each catalog value must have a unique `code`.

---

# BuyerCommercialStatus

## Description

Represents the commercial capabilities of a buyer within the platform.

A buyer's commercial status dictates whether they can add items to the cart or complete a checkout. This is independent of `UserStatus` (which handles login capability).

## Inherits From

`DomainCatalog`

## Allowed Values

| Code      | Name      | Description                                                                 |
| --------- | --------- | --------------------------------------------------------------------------- |
| ENABLED   | Enabled   | The buyer can make purchases without restrictions.                          |
| SUSPENDED | Suspended | The buyer's purchasing ability is temporarily restricted.                   |

---

# InventoryMovementType

## Description

Represents the type of inventory movement in the ledger system.

Each type defines a specific impact on the `ProductStock` (available vs reserved units).

## Inherits From

`DomainCatalog`

## Allowed Values

| Code                 | Name                 | Description                                                                           |
| -------------------- | -------------------- | ------------------------------------------------------------------------------------- |
| MANUAL_ENTRY         | Manual Entry         | Manual entry of units (initial load or restock).                                      |
| PURCHASE_RESERVATION | Purchase Reservation | Reservation of units upon order confirmation. Reduces sellable stock without exiting. |
| SALE_EXIT            | Sale Exit            | Permanent exit of units due to order dispatch.                                        |
| LOSS_ADJUSTMENT      | Loss Adjustment      | Negative adjustment due to loss, damage, or discrepancy.                              |
| RESERVATION_RELEASE  | Reservation Release  | Release of reserved units when an order is cancelled before dispatch.                 |
| RETURN_ENTRY         | Return Entry         | Re-entry of units into the warehouse following an approved physical return.           |

---

# InvoiceStatus

## Description

Represents the issuance state of a commercial tax invoice.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code   | Name   | Description                                                                                 |
| ------ | ------ | ------------------------------------------------------------------------------------------- |
| ISSUED | Issued | Invoice successfully issued after payment confirmation.                                     |
| VOIDED | Voided | Invoice voided due to order cancellation or processing of a full refund.                    |

---

# OrderStatus

## Description

Represents the complete lifecycle of an order in NexusMarket.

The lifecycle is strictly unidirectional. A completed or cancelled order cannot be modified.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code            | Name            | Description                                                                      |
| --------------- | --------------- | -------------------------------------------------------------------------------- |
| PENDING_PAYMENT | Pending Payment | Order created, awaiting payment confirmation.                                    |
| PAID            | Paid            | Payment confirmed, order enters preparation phase.                               |
| PREPARATION     | In Preparation  | Order is being picked and packed at the warehouse.                               |
| DISPATCHED      | Dispatched      | Order has left the warehouse and is in transit.                                  |
| DELIVERED       | Delivered       | Order successfully handed to the buyer.                                          |
| COMPLETED       | Completed       | Commercial cycle closed successfully.                                            |
| CANCELLED       | Cancelled       | Order cancelled before dispatch. Applies refund if payment was already processed.|

## Lifecycle

```text
PENDING_PAYMENT
      │
      ├──────────────> CANCELLED
      │
      ▼
    PAID
      │
      ├──────────────> CANCELLED
      │
      ▼
 PREPARATION
      │
      ├──────────────> CANCELLED
      │
      ▼
 DISPATCHED
      │
      ▼
 DELIVERED
      │
      ▼
 COMPLETED
```

---

# PaymentMethod

## Description

Represents the payment methods available in NexusMarket.

Payments are validated internally (mocked logic or internal wallet logic) without consuming external gateways.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code             | Name                 | Description                                                                              |
| ---------------- | -------------------- | ---------------------------------------------------------------------------------------- |
| VIRTUAL_BALANCE  | Virtual Balance      | Deducted from the buyer's virtual wallet balance.                                        |
| MOCK_CREDIT_CARD | Credit Card (Mock)   | Simulated credit card payment with internal mock validation.                             |
| MOCK_TRANSFER    | Bank Transfer (Mock) | Simulated bank transfer with internal mock validation.                                   |

---

# PaymentStatus

## Description

Represents the processing state of a payment transaction.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                                                                     |
| -------- | -------- | ----------------------------------------------------------------------------------------------- |
| PENDING  | Pending  | Transaction initiated, awaiting internal engine validation.                                     |
| APPROVED | Approved | Transaction approved. Order moves to preparation.                                               |
| REJECTED | Rejected | Transaction rejected (e.g., insufficient virtual balance, bad mock data).                       |
| REFUNDED | Refunded | Amount refunded to the buyer due to cancellation or return.                                     |

---

# ProductStatus

## Description

Represents the visibility and commercial availability of a product in the marketplace catalog.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code         | Name         | Description                                                          |
| ------------ | ------------ | -------------------------------------------------------------------- |
| PUBLISHED    | Published    | Product is visible and available for purchase.                       |
| SUSPENDED    | Suspended    | Product is temporarily hidden from the catalog.                      |
| DISCONTINUED | Discontinued | Product is permanently retired from the catalog and cannot be reactivated. |

---

# ProductType

## Description

Represents the delivery nature of a product, dictating logistical and inventory rules.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                                                              |
| -------- | -------- | ---------------------------------------------------------------------------------------- |
| PHYSICAL | Physical | Tangible product requiring warehouse inventory, packing, and physical shipment.          |
| DIGITAL  | Digital  | Intangible product delivered immediately upon payment, without physical logistics.       |

---

# RefundStatus

## Description

Represents the financial reimbursement process back to a buyer.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                                                        |
| -------- | -------- | ---------------------------------------------------------------------------------- |
| PENDING  | Pending  | Refund requested, pending review and approval.                                     |
| APPROVED | Approved | Refund approved and in process of financial execution.                             |
| EXECUTED | Executed | Refund amount successfully returned to the buyer (or credited to Virtual Balance). |

---

# ReturnStatus

## Description

Represents the physical return process of a product. Only applies to `PHYSICAL` products.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code                   | Name                   | Description                                                                            |
| ---------------------- | ---------------------- | -------------------------------------------------------------------------------------- |
| REQUESTED              | Requested              | Buyer formally requested a return.                                                     |
| IN_TRANSIT_RETURN      | In Transit - Return    | Product is on its way back to the warehouse.                                           |
| RECEIVED_AT_WAREHOUSE  | Received at Warehouse  | Warehouse received the item, pending physical inspection.                              |
| APPROVED               | Approved               | Return approved post-inspection. Generates Refund and re-enters inventory.             |
| REJECTED_DUE_TO_DAMAGE | Rejected due to Damage | Return rejected because the product arrived damaged by the buyer. No refund generated. |

## Lifecycle

```text
REQUESTED
   │
   ▼
IN_TRANSIT_RETURN
   │
   ▼
RECEIVED_AT_WAREHOUSE
   │
   ├──────────────> REJECTED_DUE_TO_DAMAGE
   │
   ▼
APPROVED ──> (Triggers Refund)
```

---

# SellerStatus

## Description

Represents a seller's operational state within the marketplace.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code         | Name         | Description                                                            |
| ------------ | ------------ | ---------------------------------------------------------------------- |
| UNDER_REVIEW | Under Review | Seller registered, pending validation by a platform Administrator.     |
| ACTIVE       | Active       | Seller approved and allowed to list products and operate.              |
| SUSPENDED    | Suspended    | Seller suspended and restricted from listing or selling products.      |

---

# ShipmentStatus

## Description

Represents the logistical progress of a physical shipment.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code            | Name            | Description                                                                   |
| --------------- | --------------- | ----------------------------------------------------------------------------- |
| PENDING_PACKING | Pending Packing | Shipment created, awaiting picking and packing in the warehouse.              |
| PACKED          | Packed          | Order fully packed and ready for dispatch.                                    |
| IN_TRANSIT      | In Transit      | Shipment dispatched and en route to the buyer's destination.                  |
| DELIVERED       | Delivered       | Shipment successfully handed over to the buyer.                               |
| FAILED          | Failed          | Delivery attempt failed (e.g., wrong address, no one available to receive).   |

---

# UserStatus

## Description

Represents the operational state of a user's system access.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                                                         |
| -------- | -------- | ----------------------------------------------------------------------------------- |
| ACTIVE   | Active   | User can log in and operate normally.                                               |
| INACTIVE | Inactive | User exists but cannot perform system operations (e.g., email not verified).        |
| BLOCKED  | Blocked  | User access suspended due to terms violation or administrative decision.            |

---

# WarehouseStatus

## Description

Represents the operational capability of a warehouse to receive or dispatch inventory.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code   | Name   | Description                                                                  |
| ------ | ------ | ---------------------------------------------------------------------------- |
| ACTIVE | Active | Warehouse is fully operational.                                              |
| CLOSED | Closed | Warehouse permanently closed. Cannot process inventory movements.            |

---

# WarehouseType

## Description

Classifies a warehouse based on its owner and operational management.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code        | Name        | Description                                                                    |
| ----------- | ----------- | ------------------------------------------------------------------------------ |
| MARKETPLACE | Marketplace | Facility directly managed by NexusMarket for proprietary or full-fulfillment operations. |
| SELLER      | Seller      | Private facility managed entirely by an independent Seller.                    |

---

# Complex Value Objects

NexusMarket utilizes several Complex Value Objects. Unlike `DomainCatalog`, these encapsulate multiple fields representing a cohesive data structure (snapshots or embedded documents).

## Address
* **Attributes:** `street`, `city`, `state`, `postalCode`, `country`, `additionalInstructions`.
* **Usage:** Embedded inside `Buyer` and `Warehouse` (MongoDB).

## DeliveryAddress
* **Attributes:** Inherits standard address fields but adds `recipientName` and `recipientPhone`.
* **Usage:** Embedded inside `Shipment` (MySQL) to decouple logistics from the buyer's main profile.

## CartItem
* **Attributes:** `productId`, `variantId`, `productName`, `variantSku`, `quantity`, `unitPrice`, `addedAt`.
* **Usage:** A snapshot of a product choice inside a `ShoppingCart`. Maintains the price at the time of addition.

## OrderLine
* **Attributes:** `productId`, `variantId`, `productName`, `variantSku`, `productTypeCode`, `quantity`, `unitPrice`, `subtotal`.
* **Usage:** An immutable snapshot attached to an `Order`. Prevents historical order corruption if a seller changes product prices or names in the future.

## ProductVariant
* **Attributes:** `variantId`, `color`, `size`, `sku`, `additionalPrice`, `imageUrl`, `active`.
* **Usage:** Differentiates specific options within a single `Product` entity.

## FiscalData
* **Attributes:** `fullNameOrCompany`, `documentType`, `documentNumber`, `email`, `fiscalAddress`, `city`, `country`.
* **Usage:** Embedded inside `Invoice` to capture tax reporting details valid under DIAN regulations.

## TaxBreakdown
* **Attributes:** `subtotal`, `taxableBase`, `vatPercentage`, `vatAmount`, `discounts`, `total`.
* **Usage:** Embedded inside `Invoice` to detail VAT structures.

---

# Primitive Enumerations

The following concepts are represented as primitive Java enumerations because they contain fixed security values specifically tied to framework integrations (e.g., Spring Security Authorities) and do not require rich business catalog metadata.

---

# UserRole

## Description

Represents the security authorization roles available in the NexusMarket platform.

## Values

```text
BUYER
SELLER
LOGISTICS_OPERATOR
ADMIN
SUPERVISOR
```

---

# Value Object Design Rules

## Immutability

All Value Objects must be immutable after creation.
Their internal state (fields) must be defined via constructors (e.g., using Lombok's `@AllArgsConstructor`) and must not offer setters that mutate the object in place.

## Equality

Value Objects are compared according to their values rather than object identity.
Two `Address` instances with the exact same street, city, and zip code are considered mathematically equal.

## Snapshotting (Historical Isolation)

Classes like `OrderLine` and `CartItem` must copy `productName`, `sku`, and `unitPrice` from the `Product` entity.
Under no circumstances should an `Order` dynamically fetch the current price of a `Product` from the database. A change in the catalog must never alter the total of an order placed yesterday.

## Controlled Values vs Magic Strings

Business concepts must use controlled `DomainCatalog` constants instead of primitive strings.

Avoid scattered string literals like:

```java
order.setStatus("PAID");
```

Instead, use the strongly typed Value Object:

```java
order.setStatus(OrderStatus.PAID);
```

This approach guarantees type safety, centralizes domain logic, prevents typos, and clearly communicates Domain-Driven Design principles across the persistence boundaries.

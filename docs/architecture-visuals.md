# Architecture Visuals: Custom Index Lifecycle

This document provides a visual representation of the **Custom Index Listing & Pricing Suite** workflow and service interactions.

## 1. High-Level Flow Diagram (The Business Process)
This diagram shows the logical progression of an index from its initial draft to its final published state, including the continuous pricing loop and the asynchronous creation entry point.

```mermaid
graph TD
    User(["User / System"]) -->|1a. REST Create| BasketSvc[Basket Management Service]
    AsyncSys(["External System"]) -->|1b. Async Request| CreateReqEvt{Basket Create Requested}
    CreateReqEvt -->|Trigger| BasketSvc
    
    subgraph "Autonomous Choreography Engine"
        BasketSvc -->|Publish| CreatedEvt{Basket Created Event}
        
        CreatedEvt -->|Trigger| ListingSvc[Listing Service]
        ListingSvc -->|Auto-List| BBG[Bloomberg / Refinitiv]
        ListingSvc -->|Publish| ListedEvt{Listing Completed Event}
        
        ListedEvt -->|Trigger| PricingSvc[Pricing Service]
        
        PricingSvc -->|Calculate NAV| MD[Market Data]
        MD -->|Data Quality Check| PricingSvc
        PricingSvc -->|Publish| PricedEvt{Basket Priced Event}
        PricedEvt -.->|Heartbeat 5s| PricingSvc
        
        PricedEvt -->|Trigger| PublishSvc[Publishing Service]
        PublishSvc -->|Integrity Check| PublishSvc
        PublishSvc -->|External Delivery| Market([Live Market])
        PublishSvc -->|Publish| FinalEvt{Basket Published Event}
        
        Admin(["Admin / System"]) -->|Decommission| DecomEvt{Basket Decommissioned Event}
        DecomEvt -->|Stop Heartbeat| PricingSvc
    end

    FinalEvt -->|Update Status| BasketSvc
    BasketSvc -->|Notify| User
```

## 2. High-Level Sequence Diagram (Service Interactions)
This diagram illustrates how the services communicate asynchronously via the Event Bus (Kafka/Solace) and the continuous update cycle.

```mermaid
sequenceDiagram
    participant U as User / Async Client
    participant B as Basket Service
    participant Bus as Event Bus (Kafka/Solace)
    participant L as Listing Service
    participant P as Pricing Service
    participant Pub as Publishing Service

    Note over U, Pub: 1. Initiation (Sync or Async)
    alt REST Path
        U->>B: POST /api/v1/baskets
        B-->>U: 201 Created (Basket ID)
    else Async Path
        U->>Bus: Emit: BasketCreateRequestedEvent
        Bus->>B: Consume: BasketCreateRequestedEvent
    end
    B->>Bus: Emit: BasketCreatedEvent

    Note over U, Pub: 2. Autonomous Execution (Choreography)
    Bus->>L: Consume: BasketCreatedEvent
    L->>L: Register with External Providers
    L->>Bus: Emit: ListingCompletedEvent

    Bus->>P: Consume: ListingCompletedEvent
    
    loop Every 5 Seconds (Heartbeat)
        P->>P: Execute Pricing & DQ Checks
        P->>Bus: Emit: BasketPricedEvent
        Bus->>Pub: Consume: BasketPricedEvent
        Pub->>Pub: Integrity Check & Market Delivery
        Pub->>Bus: Emit: BasketPublishedEvent
    end

    Note over U, Pub: 3. Finalization / Monitoring
    Bus->>B: Consume: Final Events
    B->>B: Update State (LISTED -> PRICED -> PUBLISHED)
    
    Note over U, Pub: 4. Termination
    U->>Bus: Emit: BasketDecommissionedEvent
    Bus->>P: Stop Continuous Pricing
```

## 3. Data Quality (DQ) Gatekeepers
The platform implements multiple "Gatekeeper" checks to ensure that only accurate data reaches external markets.

```mermaid
graph LR
    subgraph "Pricing Gatekeepers"
        P1[Staleness Check] --> P2[Variance Check]
        P2 --> P3[Non-Positive Price Check]
        P3 --> P4[FX Availability Check]
    end

    subgraph "Publishing Gatekeepers"
        V1[Constituent Integrity] --> V2[NAV Sanity Check]
        V2 --> V3[Format Validation]
    end

    P4 --> V1
```

## 4. Component Architecture & System Boundaries
This interaction diagram highlights the core components and how they interact logically within the platform.

```mermaid
graph LR
    subgraph "Core Business Logic"
        B[Basket Core]
        A[Analytics / RefData]
    end

    subgraph "Execution Layer"
        L[Listing Engine]
        P[Pricing Engine]
    end

    subgraph "Integration Layer"
        M[Market Data Gateway]
        Pub[Publishing Gateway]
    end

    B --- A
    B -.-> L
    L -.-> P
    P -.-> Pub
    P --- M
```

## 5. Hybrid Communication & Protocol Strategy
Following the **Smart Communication Router** strategy [[memory:6883576]], this diagram shows the specific protocols used between components based on latency and business context.

```mermaid
graph TD
    User(["External Client"]) -- "REST (latency >100ms)" --> BasketSvc[Basket Service]
    
    subgraph "Internal Infrastructure"
        BasketSvc -- "Kafka/Solace (latency 10-100ms)" --> ListingSvc[Listing Service]
        ListingSvc -- "Kafka/Solace" --> PricingSvc[Pricing Service]
        PricingSvc -- "Kafka/Solace" --> PubSvc[Publishing Service]
        
        PricingSvc -- "gRPC (latency <10ms)" --> MDSvc[Market Data Service]
        BasketSvc -- "gRPC (latency <10ms)" --> RDSvc[Ref Data Service]
        
        PricingSvc -- "Actor Model (latency <1ms)" --> NAV[NAV Calculator]
    end

    subgraph "External World"
        ListingSvc -- "FIX / REST" --> Vendors[Market Vendors]
        PubSvc -- "SFTP / API" --> Markets[Live Markets]
    end

    style NAV fill:#f9f,stroke:#333,stroke-width:2px
```

## 6. Key Architectural Principles
*   **Choreography (Not Orchestration)**: No single service "controls" the workflow. Services react to events, making the system more resilient and easier to scale.
*   **Event-Driven**: Communication is asynchronous. If one service is temporarily offline, the events are queued and processed as soon as it recovers.
*   **State-Aware**: The Basket Service tracks the aggregate state, providing a single source of truth for the User while the work happens in parallel.
*   **Smart Communication**: Protocol selection is driven by latency requirements (REST > Event Bus > gRPC > Actor Model).
*   **Root-Level Divisor**: The `divisor` is treated as a first-class attribute of the Index (Basket level) rather than an instrument property, ensuring consistent scaling of the index price.
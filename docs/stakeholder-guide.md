# Stakeholder Guide: Custom Index Listing & Pricing Suite

## 1. Executive Summary
The **Custom Index Listing & Pricing Suite** is a next-generation platform designed to automate the full lifecycle of custom synthetic indices. It enables the creation, validation, listing, pricing, and publishing of indices across multiple market data providers (e.g., Bloomberg, Refinitiv) with zero manual intervention.

## 2. Business Value (Product Perspective)
*   **Faster Time-to-Market**: Transition from index idea to live publishing in seconds rather than hours.
*   **Multi-Provider Reach**: Single-entry point to list indices across all major financial data platforms.
*   **Data Quality Assurance**: Automated "gatekeepers" ensure that only accurate, high-integrity data reaches external clients.
*   **Operational Efficiency**: Eliminates manual spreadsheets and email-based workflows through a fully choreographed event-driven engine.

## 3. The Digital Lifecycle (Workflow)
The system operates as an autonomous "Choreography" where each service knows exactly when to act:
1.  **Creation**: A new basket is saved via the API.
2.  **Validation**: The system automatically verifies instrument eligibility and reference data.
3.  **Listing**: The platform coordinates with providers (Bloomberg/Refinitiv) to register the new index.
4.  **Pricing**: Real-time pricing engines calculate the NAV using high-frequency market data.
5.  **Publishing**: The final validated index is delivered to the market.

## 4. Operational Excellence (IT Perspective)
*   **Event-Driven Resiliency**: Built on a reactive architecture (Kafka/Solace). If a service or network blips, the system automatically retries and heals without losing data.
*   **Infinite Scalability**: Designed to handle thousands of indices simultaneously by distributing the load across multiple parallel processing units.
*   **Observability**: Integrated real-time monitoring and tracing, providing a "glass box" view into the health of every index lifecycle.
*   **Security First**: Enterprise-grade security using OIDC/JWT ensures that only authorized users and systems can interact with the platform.

---
*For detailed technical documentation on the event-driven engine, see the [Spring Cloud Stream Guide](cloud-stream-guide.md). For architectural diagrams, see [Architecture Visuals](architecture-visuals.md).*

## 5. Future-Proofing
The platform is built on a "Pluggable Architecture." This means adding a new market data provider or a new pricing engine requires minimal effort and no changes to the core system logic.

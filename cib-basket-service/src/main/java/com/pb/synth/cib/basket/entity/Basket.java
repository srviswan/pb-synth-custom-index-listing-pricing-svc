package com.pb.synth.cib.basket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "basket")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String status;

    @Column(name = "source_system", nullable = false)
    private String sourceSystem;

    private String owner;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Constituent> constituents = new ArrayList<>();

    public void addConstituent(Constituent constituent) {
        constituents.add(constituent);
        constituent.setBasket(this);
    }

    public void removeConstituent(Constituent constituent) {
        constituents.remove(constituent);
        constituent.setBasket(null);
    }
}

package com.hydra.spark.sample.persistence.domain;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_last_modified")
    protected Date modificationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    protected Date creationDate;

    @Version
    @Column(name = "version")
    protected Integer version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @PrePersist
    public void prePersist() {
        this.modificationDate = new Date();
        if (this.creationDate == null) {
            this.creationDate = new Date();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.modificationDate = new Date();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Base other = (Base) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}

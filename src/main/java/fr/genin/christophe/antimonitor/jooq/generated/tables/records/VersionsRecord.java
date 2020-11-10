/*
 * This file is generated by jOOQ.
 */
package fr.genin.christophe.antimonitor.jooq.generated.tables.records;


import fr.genin.christophe.antimonitor.jooq.generated.tables.Versions;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class VersionsRecord extends UpdatableRecordImpl<VersionsRecord> implements Record4<String, String, String, JSON> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.versions.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.versions.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.versions.idproject</code>.
     */
    public void setIdproject(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.versions.idproject</code>.
     */
    public String getIdproject() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.versions.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.versions.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.versions.document</code>.
     */
    public void setDocument(JSON value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.versions.document</code>.
     */
    public JSON getDocument() {
        return (JSON) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, JSON> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<String, String, String, JSON> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Versions.VERSIONS.ID;
    }

    @Override
    public Field<String> field2() {
        return Versions.VERSIONS.IDPROJECT;
    }

    @Override
    public Field<String> field3() {
        return Versions.VERSIONS.NAME;
    }

    @Override
    public Field<JSON> field4() {
        return Versions.VERSIONS.DOCUMENT;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getIdproject();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public JSON component4() {
        return getDocument();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getIdproject();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public JSON value4() {
        return getDocument();
    }

    @Override
    public VersionsRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public VersionsRecord value2(String value) {
        setIdproject(value);
        return this;
    }

    @Override
    public VersionsRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public VersionsRecord value4(JSON value) {
        setDocument(value);
        return this;
    }

    @Override
    public VersionsRecord values(String value1, String value2, String value3, JSON value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached VersionsRecord
     */
    public VersionsRecord() {
        super(Versions.VERSIONS);
    }

    /**
     * Create a detached, initialised VersionsRecord
     */
    public VersionsRecord(String id, String idproject, String name, JSON document) {
        super(Versions.VERSIONS);

        setId(id);
        setIdproject(idproject);
        setName(name);
        setDocument(document);
    }
}
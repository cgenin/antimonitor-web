/*
 * This file is generated by jOOQ.
 */
package fr.genin.christophe.antimonitor.jooq.generated.tables.records;


import fr.genin.christophe.antimonitor.jooq.generated.tables.FrontApps;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FrontAppsRecord extends UpdatableRecordImpl<FrontAppsRecord> implements Record5<String, String, LocalDate, String, JSON> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.front_apps.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.front_apps.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.front_apps.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.front_apps.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.front_apps.latest</code>.
     */
    public void setLatest(LocalDate value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.front_apps.latest</code>.
     */
    public LocalDate getLatest() {
        return (LocalDate) get(2);
    }

    /**
     * Setter for <code>public.front_apps.version</code>.
     */
    public void setVersion(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.front_apps.version</code>.
     */
    public String getVersion() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.front_apps.document</code>.
     */
    public void setDocument(JSON value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.front_apps.document</code>.
     */
    public JSON getDocument() {
        return (JSON) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, String, LocalDate, String, JSON> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<String, String, LocalDate, String, JSON> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return FrontApps.FRONT_APPS.ID;
    }

    @Override
    public Field<String> field2() {
        return FrontApps.FRONT_APPS.NAME;
    }

    @Override
    public Field<LocalDate> field3() {
        return FrontApps.FRONT_APPS.LATEST;
    }

    @Override
    public Field<String> field4() {
        return FrontApps.FRONT_APPS.VERSION;
    }

    @Override
    public Field<JSON> field5() {
        return FrontApps.FRONT_APPS.DOCUMENT;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public LocalDate component3() {
        return getLatest();
    }

    @Override
    public String component4() {
        return getVersion();
    }

    @Override
    public JSON component5() {
        return getDocument();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public LocalDate value3() {
        return getLatest();
    }

    @Override
    public String value4() {
        return getVersion();
    }

    @Override
    public JSON value5() {
        return getDocument();
    }

    @Override
    public FrontAppsRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public FrontAppsRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public FrontAppsRecord value3(LocalDate value) {
        setLatest(value);
        return this;
    }

    @Override
    public FrontAppsRecord value4(String value) {
        setVersion(value);
        return this;
    }

    @Override
    public FrontAppsRecord value5(JSON value) {
        setDocument(value);
        return this;
    }

    @Override
    public FrontAppsRecord values(String value1, String value2, LocalDate value3, String value4, JSON value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FrontAppsRecord
     */
    public FrontAppsRecord() {
        super(FrontApps.FRONT_APPS);
    }

    /**
     * Create a detached, initialised FrontAppsRecord
     */
    public FrontAppsRecord(String id, String name, LocalDate latest, String version, JSON document) {
        super(FrontApps.FRONT_APPS);

        setId(id);
        setName(name);
        setLatest(latest);
        setVersion(version);
        setDocument(document);
    }
}

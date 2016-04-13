package com.example.anticorruption;

import android.os.Bundle;

/**
 * Created by alexlondon on 4/13/16.
 */
public class Institution {
    public String id;
    public String name;
    public String address;
    public String city;
    public String type;
    public String logo;
    public String phone;
    public String manager;
    public String positive;
    public String negative;
    public String views;
    public String abuse_of_discretion;
    public String blackmail;
    public String bribery;
    public String embezzlement;
    public String extortion;
    public String fraud;
    public String nepotism;
    public String other_corruption;
    public String appointment_wait_time;
    public String appointment_entries;
    public String document_wait_time;
    public String document_entries;
    public String permit_wait_time;
    public String permit_entries;
    public String table_name;

    public Institution (Bundle mBundle) throws NullPointerException {
        id = (mBundle.getString("ID"));
        name = mBundle.getString("INSTITUTION");
        address = mBundle.getString("ADDRESS");
        city = mBundle.getString("CITY");
        type = mBundle.getString("TYPE");
        logo = mBundle.getString("LOGO");
        phone = mBundle.getString("PHONE");
        manager = mBundle.getString("MANAGER");
        positive = mBundle.getString("POSITIVE");
        negative = mBundle.getString("NEGATIVE");
        views = (mBundle.getString("VIEWS"));
        abuse_of_discretion = (mBundle.getString("ABUSE_OF_DISCRETION"));
        blackmail = (mBundle.getString("BLACKMAIL"));
        bribery = (mBundle.getString("BRIBERY"));
        embezzlement = (mBundle.getString("EMBEZZLEMENT"));
        extortion = (mBundle.getString("EXTORTION"));
        fraud = (mBundle.getString("FRAUD"));
        nepotism = (mBundle.getString("NEPOTISM"));
        other_corruption = (mBundle.getString("OTHER_CORRUPTION"));
        appointment_wait_time = mBundle.getString("APPOINTMENT_WAIT_TIME");
        appointment_entries = (mBundle.getString("APPOINTMENT_ENTRIES"));
        document_wait_time = mBundle.getString("DOCUMENT_WAIT_TIME");
        document_entries = (mBundle.getString("DOCUMENT_ENTRIES"));
        permit_wait_time = mBundle.getString("PERMIT_WAIT_TIME");
        permit_entries = (mBundle.getString("PERMIT_ENTRIES"));
        table_name = mBundle.getString("TABLE_NAME");
    }

    public String getCorruptionTypeValue(String mCorruptionType) {
        if (mCorruptionType != null && !mCorruptionType.equals("null")) {
            return mCorruptionType;
        } else {
            return "0";
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getTable_name() {
        return table_name;
    }

    public String getAppointment_wait_time() {
        return appointment_wait_time;
    }

    public String getPermit_wait_time() {
        return permit_wait_time;
    }

    public String getLogo() {
        return logo;
    }

    public String getManager() {
        return manager;
    }

    public String getPhone() {
        return phone;
    }

    public String getDocument_wait_time() {
        return document_wait_time;
    }

    public String getType() {
        return type;
    }

    public String getViews() {
        if (views != null && !views.equals("null")) {
            return views;
        } else {
            return "0";
        }
    }

    public String getPermit_entries() {
        if (permit_entries != null && !permit_entries.equals("null")) {
            return permit_entries;
        } else {
            return "0";
        }
    }

    public String getAppointment_entries() {
        if (appointment_entries != null && !appointment_entries.equals("null")) {
            return appointment_entries;
        } else {
            return "0";
        }
    }

    public String getDocument_entries() {
        if (document_entries != null && !document_entries.equals("null")) {
            return document_entries;
        } else {
            return "0";
        }
    }

    public float getPositive() {
        if (positive != null && !positive.equals("null")) {
            return Float.valueOf(positive);
        } else {
            return 0f;
        }
    }

    public float getNegative() {
        if (negative != null && !negative.equals("null")) {
            return Float.valueOf(negative);
        } else {
            return 0f;
        }
    }
}
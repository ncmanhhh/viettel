package com.datn.viettel.configs.hibernate;


import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

public class PgVectorFloatArrayType implements UserType<float[]> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<float[]> returnedClass() {
        return float[].class;
    }

    @Override
    public boolean equals(float[] x, float[] y) {
        return Arrays.equals(x, y);
    }

    @Override
    public int hashCode(float[] x) {
        return Arrays.hashCode(x);
    }

    @Override
    public float[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        Object obj = rs.getObject(position);
        if (obj == null) return null;

        String s = obj.toString().trim().replace("[", "").replace("]", "");
        if (s.isBlank()) return new float[0];

        String[] parts = s.split(",");
        float[] arr = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Float.parseFloat(parts[i].trim());
        }
        return arr;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, float[] value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }

        // pgvector format: [1,2,3]
        String vectorText = Arrays.toString(value).replace(" ", "");

        PGobject pg = new PGobject();
        pg.setType("vector");
        pg.setValue(vectorText);

        st.setObject(index, pg);
    }

    //Hàm sao chép mảng float
    @Override
    public float[] deepCopy(float[] value) {
        return value == null ? null : Arrays.copyOf(value, value.length);
    }

    @Override
    public boolean isMutable() { return true; }

    @Override
    public Serializable disassemble(float[] value) { return deepCopy(value); }

    @Override
    public float[] assemble(Serializable cached, Object owner) { return deepCopy((float[]) cached); }

    @Override
    public float[] replace(float[] detached, float[] managed, Object owner) { return deepCopy(detached); }
}
package com.tomek.luckynumber.rest.client;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import com.tomek.luckynumber.rest.api.ConnectionInterface;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by tomek on 27.07.15.
 */
public class RestClient {
    public static final String BASE_URL = "";

    private RestAdapter restAdapter;
    private ConnectionInterface connectionInterface;

    public RestClient() {

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setConverter(new StringConverter())
                .build();

        connectionInterface = restAdapter.create(ConnectionInterface.class);
    }

    public ConnectionInterface getConnectionInterface() {
        return connectionInterface;
    }


    private class StringConverter implements Converter {

        @Override
        public Object fromBody(TypedInput typedInput, Type type) throws ConversionException {
            String text;
            try {
                text = fromStream(typedInput.in());
            } catch (IOException e) {
                Log.d(StringConverter.class.getName(), e.toString());
                throw new ConversionException(StringConverter.class.toString());
            }
            return text;
        }

        @Override
        public TypedOutput toBody(Object o) {
            throw new UnsupportedOperationException();
        }

        private String fromStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }
            return out.toString();
        }

    }
}

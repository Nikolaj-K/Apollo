/*
 * Copyright © 2018 Apollo Foundation
 */

package com.apollocurrency.aplwallet.apl;

import com.apollocurrency.aplwallet.apl.util.Logger;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TransactionDeserializer extends StdDeserializer<JSONTransaction> {
        public TransactionDeserializer() {
            this(null);
        }

        public TransactionDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public JSONTransaction deserialize(JsonParser jp, DeserializationContext ctxt) {
            JSONObject json = null;
            PrintStream out = System.out;
            System.setOut(new PrintStream(new ByteArrayOutputStream(1024*1024*16)));
            try {
                JsonNode node = jp.getCodec().readTree(jp);
                JSONParser parser = new JSONParser();
                json = (JSONObject) parser.parse(node.toString());
                if (json != null) {
                    try {
                        json.get("type");
                    }
                    catch (Exception e) {
                        return null;
                    }
                } else return null;
                TransactionImpl transaction = TransactionImpl.newTransactionBuilder(json).build();
                int numberOfConfirmations = -1;
                try {
                    numberOfConfirmations = Integer.parseInt(json.get("confirmations").toString());
                }
                catch (Exception e) {
                    Logger.logDebugMessage("Got unconfirmed transaction");
                }
                int height = Integer.parseInt(json.get("height").toString());
                transaction.setHeight(height);
                JSONTransaction jsonTransaction = new JSONTransaction(transaction);
                jsonTransaction.setNumberOfConfirmations(numberOfConfirmations);
                return jsonTransaction;
            }
            catch (Exception e) {
                //ignore
                return null;
            } finally {

            }
        }
}
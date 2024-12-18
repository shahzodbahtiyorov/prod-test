package org.finance.data.model.tkb.tranfer;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class TkbTranferCreateModel {


        @SerializedName("result")
        private Result result;

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public static class Result {

            @SerializedName("form_url")
            private String formUrl;

            @SerializedName("ext_id")
            private String extId;

            public String getFormUrl() {
                return formUrl;
            }



            public String getExtId() {
                return extId;
            }


        }
    }



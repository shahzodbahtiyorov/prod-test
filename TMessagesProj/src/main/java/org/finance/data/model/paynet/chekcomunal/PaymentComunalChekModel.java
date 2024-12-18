package org.finance.data.model.paynet.chekcomunal;
import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class PaymentComunalChekModel {


    @SerializedName("jsonrpc")
    private String jsonrpc;

    @SerializedName("result")
    private Result result;

    @SerializedName("id")
    private String id;

    @SerializedName("status")
    private boolean status;

    @SerializedName("origin")
    private String origin;

    @SerializedName("host")
    private Host host;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public static class Result {
        @SerializedName("sender")
        private Object sender;

        @SerializedName("receiver")
        private Receiver receiver;

        @SerializedName("support_info")
        private Object supportInfo;

        @SerializedName("state")
        private int state;

        @SerializedName("amount")
        private int amount;

        @SerializedName("description")
        private String description;

        @SerializedName("tr_id")
        private long trId;

        @SerializedName("commission")
        private int commission;

        @SerializedName("cheque")
        private Cheque cheque;

        public Object getSender() {
            return sender;
        }

        public void setSender(Object sender) {
            this.sender = sender;
        }

        public Receiver getReceiver() {
            return receiver;
        }

        public void setReceiver(Receiver receiver) {
            this.receiver = receiver;
        }

        public Object getSupportInfo() {
            return supportInfo;
        }

        public void setSupportInfo(Object supportInfo) {
            this.supportInfo = supportInfo;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getTrId() {
            return trId;
        }

        public void setTrId(long trId) {
            this.trId = trId;
        }

        public int getCommission() {
            return commission;
        }

        public void setCommission(int commission) {
            this.commission = commission;
        }

        public Cheque getCheque() {
            return cheque;
        }

        public void setCheque(Cheque cheque) {
            this.cheque = cheque;
        }
    }

    public static class Receiver {
        @SerializedName("id")
        private long id;

        @SerializedName("service_id")
        private int serviceId;

        @SerializedName("time")
        private double time;

        @SerializedName("fields")
        private Fields fields;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getServiceId() {
            return serviceId;
        }

        public void setServiceId(int serviceId) {
            this.serviceId = serviceId;
        }

        public double getTime() {
            return time;
        }

        public void setTime(double time) {
            this.time = time;
        }

        public Fields getFields() {
            return fields;
        }

        public void setFields(Fields fields) {
            this.fields = fields;
        }
    }

    public static class Fields {
        @SerializedName("soato")
        private String soato;

        @SerializedName("customer_code")
        private String customerCode;

        public String getSoato() {
            return soato;
        }

        public void setSoato(String soato) {
            this.soato = soato;
        }

        public String getCustomerCode() {
            return customerCode;
        }

        public void setCustomerCode(String customerCode) {
            this.customerCode = customerCode;
        }
    }}




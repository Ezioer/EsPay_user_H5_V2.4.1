package com.hdtx.androidsdk.androidsdk.http;

public class DevicesInfo {

    /**
     * customDeviceId : 54FCAD402AFE691FD423B020BC91B4B1C3C1C3D4
     * isCustom : 1
     * deviceInfo : {"product":"aosp_fajita","serial":"unknown","imei":"","model":"ONEPLUS A6013","id":"SD1A.210817.036","uuid":"ffffffffb507f9c7ffffffffef05ac4a","brand":"OnePlus","device":"fajita","androidId":"4519061f14324338","board":"sdm845","hardware":"qcom"}
     */
    private String customDeviceId;
    private int isCustom;
    private DeviceInfoEntity deviceInfo;

    public void setCustomDeviceId(String customDeviceId) {
        this.customDeviceId = customDeviceId;
    }

    public void setIsCustom(int isCustom) {
        this.isCustom = isCustom;
    }

    public void setDeviceInfo(DeviceInfoEntity deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getCustomDeviceId() {
        return customDeviceId;
    }

    public int getIsCustom() {
        return isCustom;
    }

    public DeviceInfoEntity getDeviceInfo() {
        return deviceInfo;
    }

    public class DeviceInfoEntity {
        /**
         * product : aosp_fajita
         * serial : unknown
         * imei :
         * model : ONEPLUS A6013
         * id : SD1A.210817.036
         * uuid : ffffffffb507f9c7ffffffffef05ac4a
         * brand : OnePlus
         * device : fajita
         * androidId : 4519061f14324338
         * board : sdm845
         * hardware : qcom
         */
        private String product;
        private String serial;
        private String imei;
        private String model;
        private String id;
        private String uuid;
        private String brand;
        private String device;
        private String androidId;
        private String board;
        private String hardware;

        public void setProduct(String product) {
            this.product = product;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public void setAndroidId(String androidId) {
            this.androidId = androidId;
        }

        public void setBoard(String board) {
            this.board = board;
        }

        public void setHardware(String hardware) {
            this.hardware = hardware;
        }

        public String getProduct() {
            return product;
        }

        public String getSerial() {
            return serial;
        }

        public String getImei() {
            return imei;
        }

        public String getModel() {
            return model;
        }

        public String getId() {
            return id;
        }

        public String getUuid() {
            return uuid;
        }

        public String getBrand() {
            return brand;
        }

        public String getDevice() {
            return device;
        }

        public String getAndroidId() {
            return androidId;
        }

        public String getBoard() {
            return board;
        }

        public String getHardware() {
            return hardware;
        }
    }
}

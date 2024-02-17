package com.hn.cat.Entry;

public class MarryEntry {
    private Long husbandId;
    private Long wifeId;

    private MarryEntry(Long husbandId, Long wifeId) {
        this.husbandId = husbandId;
        this.wifeId = wifeId;
    }
    public static MarryBuilder builder(){
        return new MarryBuilder();
    }
    public static class MarryBuilder{
        Long husbandId;
        Long wifeId;

        public MarryBuilder WifeId(Long wifeId) {
            this.wifeId = wifeId;
            return this;
        }

        public MarryBuilder HusbandId(Long husbandId) {
            this.husbandId = husbandId;
            return this;
        }
        public MarryEntry build(){
            return new MarryEntry(husbandId,wifeId);
        }
    }

    public Long getHusbandId() {
        return husbandId;
    }

    public void setHusbandId(Long husbandId) {
        this.husbandId = husbandId;
    }

    public Long getWifeId() {
        return wifeId;
    }

    public void setWifeId(Long wifeId) {
        this.wifeId = wifeId;
    }
}

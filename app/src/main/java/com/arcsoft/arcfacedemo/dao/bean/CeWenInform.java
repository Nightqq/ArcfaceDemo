package com.arcsoft.arcfacedemo.dao.bean;

import com.arcsoft.arcfacedemo.util.server.net.NetWorkUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CeWenInform {

    @Id
    private String ip = NetWorkUtils.getIP();
    private String name = "张三";
    private String emp_id = "2859CD";
    private String temperature = "0";
    private String photo = "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";
    private String state = "进";
    private String time = "65464646464";

    @Generated(hash = 1313765761)
    public CeWenInform(String ip, String name, String emp_id, String temperature,
                       String photo, String state, String time) {
        this.ip = ip;
        this.name = name;
        this.emp_id = emp_id;
        this.temperature = temperature;
        this.photo = photo;
        this.state = state;
        this.time = time;
    }

    @Generated(hash = 2145803294)
    public CeWenInform() {
    }



    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmp_id() {
        return this.emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public void setnoPhoto() {
        this.photo = "iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAIAAAC2BqGFAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAXFSURBVHhe7ZxJbtwwEEVzkCxzGq98Fi98lgZ8lMBHSW6QRcMLw0AAR9RQLJLFSWL/tOD/wEVbljg8FosyY+fbJ4FA0SAoGgRFg6BoEBQNgqJBUDQIigZB0SAoGgRFg6BoEBQNgqJBUDQIigZB0SAoGgRFg6BoEBQNgqJBUDQIigZB0SAoGgRFg6BoEBQNgqJBDBH9+vb9x5+5vP1cL/Xx6+W61vD4/mu9NgjpW6Zm3/TezrcxRPTv94djfe0Vrez8eXj5u141EdHPH+uVkJ/Paz25GwYxVvTueCzGnXdhl+vl93qnQUX038tjQyUDuDfRmw4ds9nimnOmSkFdFn14LTbTI7oWWaXy9LpWYpPqCK9I01M9MgeVpLFgiJYorpSm+lv5iqJbB1Lpcx//MXX4ZWuUh8ctdfSLrqWd60MxL8vjFE3R4zZDSOqg6OTKFxHduk1XS1YNRc8gRb8vbT09Z0Wb7rLom+fPUx/uVnQea0NrCrQI0bGV8aK3okRXyv2IzkW6HSkmEj6+LIk+tBmIbt4SEqHrj38nE13sbsNPtObr3RakUSIORH9+PK33W60kIbwWFf5bbWdIHSoSr5dXCbG3J4nxhjcQP+CXOBsURctKMk3pdWbUPHEe0SpqXG+CtSzh1uR6JUm7odnsl/Z+MNUmTSc1T5xDtIrlbZxR0gwWb9upWKxDZmt1EYn291fn8qSiZcCuSNfT3SlwnYk7TaTDp+94+9oGH89ElhOKVjkhcpeKnvCytqKGGhPq8ItmqzARraa8UO3EqUTHr3FxD0zRDuP9L5tVlxucDj+jcnMqWk9kyUhJdKXARQexaUVBVvRMgxEfws8f/rNvy0+YrkHdmZdyutThms8tUhlM49bnSUL+8eon1TdnizZjfyNIdK6cRXSJ/aL1s+7xixHOE150LNQ9bvuK8oO21ijaznI7GSL6EFPoxQN2InKrp51lCo1sJtNmi74N/1/0F4GiQVA0CIoGQdEgKBoERYOgaBAUDYKiQSBE+/OdvmMadQwy9HwnBNMKRHTu7K2GP1/dcSiRnN6txTpXyXxrKG2i9clvraQHeMdF9x8KqkbDEtkM5mPocV1Ej2jRFH25sYytILozZMaIXp61+5AE0I6GGtmTOlSwVP5JpTTIOjcX7cM5+nXsG7BDdHhU74YRnb6LoHUa7lS0z85O7t4du5Vu0VGHZAAquseL7nzQURPtwyW9cgvXnaLDKFhQ+8lycZTo1EUHRdF+CoMNUL2BDHfdIVr6mqTmKBYGidY7Vf+wVW+DMvVBRUY0kOip+LtHaBPd93o3SLSOr/4XL2k0iujOMmxvbIxotdbqRX5l4JDog8GVE61+vzRb1j/bGpo9WlPHttzWGTb26CXqXegNiehkajuHnRfds6rG0bkZbhiiPSNEG8mqL6h3inb5qnv1tHCnouWRaYm8l9rKUha9fDl9Swair89lWHZeuE/R/jXGaVLRvVhrQSkLytSHoD+y5c4DibwPpDdH14rr7lHRvq3tZSO9UqUg2s/c1Fv5bO0uA+kTLab8yCWil9A4LtqO3+4f26SHVuqQ2qb8oD4H0gdzb6kjK1RHaEMCkT6YonUP1efLmkaaAqKTm4o2B1nAP2juRb7RumuZMHuydbzL5+1/PRifNybGip6Hl6y7NtHacm6oKt7LtSVJIO5DuAfOSAcGv28sHBadliTGG0QHBovRGtyZk5LGQUcfjDU6gCbR0stqMYfh16b13bDylmUbuU7V+Btkzuqit0WQn4lDtEW0ehPwpbI1JzrSMehjI1c61my0pHTNaub0S8WuMi66G1PH3N3OVpN1YESrv2fHkOwY9Ll+ua67of6auq3ARe9iC9gbLcZzcUvRREHRICgaBEWDoGgQFA2CokFQNAiKBkHRICgaBEWDoGgQFA2CokFQNAiKBkHRICgaBEWDoGgQFA2CokFQNAiKBkHRICgaBEWDoGgIn5//AKClC0X2ceqWAAAAAElFTkSuQmCC";
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}

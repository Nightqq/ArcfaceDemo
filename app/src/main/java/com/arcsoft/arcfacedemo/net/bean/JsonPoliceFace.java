package com.arcsoft.arcfacedemo.net.bean;

import java.util.List;

public class JsonPoliceFace {


    /**
     * ip : 192.168.0.89
     * data : [{"emp_name":"郑晨","emp_id":"143136","photo":"AAD6RAAAdEOccDy9wHSnPNTfcbwTFCE9n6IMPCNXizzcgKo8j1jWvFqqV725zr88ZHbgvXYaRT0D/ua8PBAwPdKoIb3TcHs8lPDBvYGzqj3+wYu8x20BvPTKpT2EzJi9XeyOu0bs5L14goe6N4GMvA+cpT1guSQ9eUMbPqwbuTwUV2W8gIskvQxC8Lxisay9ofEyvOHvEjyl3w88LoClPMf3jb1GtHC9CUOYvXsohbzHf+A9sdoLPiTQob1JGg097Vm8vBRkrD1vct88tjMpPWXsGT25WM+8xghluq+4Hz2G7IE9xMkDPl+i6DtxEE89wp6tPMOWdz2qRU29TGr/vBGMXLz4QA0+qjxtPZ9YzjzJW9a9Ga2sverXCb6NPJi8RkghPn90eT3HrKW8PLBTvR//WL2nXrY9FGWYvUm6Ej0Z+Ec9GSk8vHrT1738UeM8/ya3vU3KebyXWna9/lKXPYMxkz1nikQ958ipvTF+YDxwTki8R9AAvpPIhDyidKa8FptVPN9Dgrx1fy29QTESvXO6Sj0PTbm9xx/2OsfFUD3q7Um9ucYQvT4NNb008VU+fPQDPk2qoLqniKQ7LxzUPSB55LmdKqK93x1JvWBGDjzfxAG9lv/RPLohgTvgSK+83651PeNBcD2Tgsu8UYNQvSzHWT1h8ze9ESROvQOCej1ni0A87KqJvAd3GDzz/7Y8dFyOPSr2kr0xoC699FuUPYeXbD0MPKW9HMvwvEwXs739LA49Q0r5vBCAdjt8v9q8Fi04PU/giz2Em1W96gQMPeBTNzz+X6Y9aRyKve2Plzw5tMo9/DXNPcdD8DyaXJq8+BgIvHkybT1qM/o8fJWTPX17lT3Pl0q8fxFYPfi3BTwhQps96PkRPf+8Xj2Bz1I75kDBPeHeML19xDa9mQXOvbPGSD1VAbU8Wm7tvcDN7rx7gh897nQdvW89ijyzJ3Q95CgMvph5Bj4ciII9mkFeuYF87b2cjh89IfaoPUsutj0kH0o9RW83PflwyT2QpUO9J8daPRRpkr0Gtve8rq4jvfnJcj2xWYU8J2PovAw5qrzg/1U9cTm6vESXBj0D+jo9DwYWvIDCMDzJ1yS9QG0DPhrNqjtHDoW97dEWPc0tZDvfAIu7F9IOPSm8uj3kaJ49XBKuPSxvDj2AeZu87+59PWBUz7te7Ye94jmxPSBzyLyWMOC8ZbYAPSSKEbyjndu9p1KYu6mGNr2wT6Q7ZJ6APaE/3j1ZnB462b4OPkSr3zxydiM+eaahvLv1Bby54MM9irmkvCB5wTxJcgw+5eQfPl/3F7xasc87cWf/vf0qsjzRZ2c817UFvYDldz0/EAg8YvuCPSROqj3HyGU8OYRLvDz+Wz1s6wi8"},{"emp_name":"丁术成","emp_id":"18C449","photo":"AAD6RAAAdEN3+4Y9PIkNvHXKRLx9jw+9CRTUu5PwALvtoKk967FZPcSD7ruedny8MGHMvUV8lj1y+s67hJ67O3iiIT1k/2c8liM+vCLU77yBHZo9c1y7PX33ZT2f1qa9QTn4vTkz9rxFEsA91henvdJFlj0x60U8FnMPvZu50Dz4x4S871TUPRIZA73NZhM9M11HPW5WQbwAt6W7MUO3vAXYaD15FnK8/3U3vZSiizwB3wE9wrVBvqDhMrwEqSw9ygbSvaSSiTzqN4o9kCXGu6asCb7CLgi98L3dvAYH8L0vHqG9D1iLPW2Zpr2+FWs9DHJ6vebgPrz+Jbe9kzkcPc0yl72CXwI74ZzEvGkXVr1h3FY9hIFBvfYuTz0Ldhu87KumPTEST72tlSm96avgvX0pyLzwnaK9i70CveVS4Lv06208+AJTPKU2AL2NIvE843MMviIDKLzATU29Or8SPMtMqT3tECw8pcYYPViNpb2LeVO9pm3Kve3XrLsygf67M3HtvRSYJD181FQ9oF0RPcRg9D2osLM8WPU/vaYPHb2Zo6a6agC0vVR7B72DtO49oPuEvNeKlbx5W8M5j3bkPNHyKD148kg9brQUPjCF+j0H3Z29HDucPZXirjzJRbu5Na/BvPozZD0/LIe9YYJKveF6Cj5CiSQ6QAfRu0hdnDwX/A29eLjAO/uMh71uIgM9H48avJ2kRb2Qbeu8bHMAvhSHi73IqdQ8c1ZNvTgsA7zeGb89SjCbvCIyH72aHAO9jM9LPQpqaztgHN48Ct2CPZkdK73heuo8noCkPG9HebwozMo8tDbGPd5ovz2Dl5u90MvrvEb/m71+GTI8/m5/Ow+CNb0cySK93smnPSqtIL1DDdq9adycPYV8/TyqzE28JZiqvbZFAD02SUm9wTO2vRLlFj7n5og9gwtiO/AquT0rv3W8aSaHPKVc3bstPTe97ZEsPey+lryMm0K9mw7WPS5/J77bVFM8CsZyPQ1URD1Kx7g88fREvLxWl7x/BsW8afgUvvDrkj1ddp47M2QGPFthC71vwOs8k6EevVI19ryIiq09tvy8vdsDCrw3qw4+HHHwvPlRLr2PNY49NhxGvciSyTyYw5C9SJqlPabABT2JmIy9Jb8CPjG0rD3+WcK8sqVJPKTt/bvLfyQ+XwcqvRCIjz21phy97CS6PQ+/zLwvSn09RBBgPeVnwbwbuek8aa81PDr7Mj2TMGA9UMBcPWeozT2nAde8CL1lvQjye7xQFJA9i7HqvES4xjzFICs+vI9NPWtAqbzJNPu8jA8WPRfXNDxY21a9GWa9vcrStr1znh89rr+GPOcvG7x16OG6igXRPQtPvz23hd09e2v8vWi96Dz1M9c8"},{"emp_name":"薛永东","emp_id":"1B10D1","photo":"AAD6RAAAdEO6xg8+tSeTuUqo89BiCTPdIBAb0bkq69y83MPSNQED5TR8o9Sx2VPPrnxDyl4 / g8tpo3Pa3X57wqK8Q9BWeIvbGZ1Dxp0fE9q9NMPLQNN72gXY691QLnPDryjL2PidW83y3evQVVP70VrdC9Vy6 + Pbv + njyVdAO7hDxnOz50S7qypog9sduvvHEhyLw79Ro9QuR7vVbXjjwYIp09bBuMPfy4jrzMnR + 9 x0fAvHAimr3gs8I9NI2VPdm2qLwc + wo9nXebvdu2i7ufDI687wRxPcdJtT2lBIm9NSgxvXbB3T1HBVI9vJ97PE1 / tzxmP6m90FZtPRT + jL0HeQY + w04wvplP1rzEMSs9 "}]
     */

    private String ip;
    private List<DataBean> data;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * emp_name : 郑晨
         * emp_id : 143136
         * photo : AAD6RAAAdEOccDy9wHSnPNTfcbwTFCE9n6IMPCNXizzcgKo8j1jWvFqqV725zr88ZHbgvXYaRT0D/ua8PBAwPdKoIb3TcHs8lPDBvYGzqj3+wYu8x20BvPTKpT2EzJi9XeyOu0bs5L14goe6N4GMvA+cpT1guSQ9eUMbPqwbuTwUV2W8gIskvQxC8Lxisay9ofEyvOHvEjyl3w88LoClPMf3jb1GtHC9CUOYvXsohbzHf+A9sdoLPiTQob1JGg097Vm8vBRkrD1vct88tjMpPWXsGT25WM+8xghluq+4Hz2G7IE9xMkDPl+i6DtxEE89wp6tPMOWdz2qRU29TGr/vBGMXLz4QA0+qjxtPZ9YzjzJW9a9Ga2sverXCb6NPJi8RkghPn90eT3HrKW8PLBTvR//WL2nXrY9FGWYvUm6Ej0Z+Ec9GSk8vHrT1738UeM8/ya3vU3KebyXWna9/lKXPYMxkz1nikQ958ipvTF+YDxwTki8R9AAvpPIhDyidKa8FptVPN9Dgrx1fy29QTESvXO6Sj0PTbm9xx/2OsfFUD3q7Um9ucYQvT4NNb008VU+fPQDPk2qoLqniKQ7LxzUPSB55LmdKqK93x1JvWBGDjzfxAG9lv/RPLohgTvgSK+83651PeNBcD2Tgsu8UYNQvSzHWT1h8ze9ESROvQOCej1ni0A87KqJvAd3GDzz/7Y8dFyOPSr2kr0xoC699FuUPYeXbD0MPKW9HMvwvEwXs739LA49Q0r5vBCAdjt8v9q8Fi04PU/giz2Em1W96gQMPeBTNzz+X6Y9aRyKve2Plzw5tMo9/DXNPcdD8DyaXJq8+BgIvHkybT1qM/o8fJWTPX17lT3Pl0q8fxFYPfi3BTwhQps96PkRPf+8Xj2Bz1I75kDBPeHeML19xDa9mQXOvbPGSD1VAbU8Wm7tvcDN7rx7gh897nQdvW89ijyzJ3Q95CgMvph5Bj4ciII9mkFeuYF87b2cjh89IfaoPUsutj0kH0o9RW83PflwyT2QpUO9J8daPRRpkr0Gtve8rq4jvfnJcj2xWYU8J2PovAw5qrzg/1U9cTm6vESXBj0D+jo9DwYWvIDCMDzJ1yS9QG0DPhrNqjtHDoW97dEWPc0tZDvfAIu7F9IOPSm8uj3kaJ49XBKuPSxvDj2AeZu87+59PWBUz7te7Ye94jmxPSBzyLyWMOC8ZbYAPSSKEbyjndu9p1KYu6mGNr2wT6Q7ZJ6APaE/3j1ZnB462b4OPkSr3zxydiM+eaahvLv1Bby54MM9irmkvCB5wTxJcgw+5eQfPl/3F7xasc87cWf/vf0qsjzRZ2c817UFvYDldz0/EAg8YvuCPSROqj3HyGU8OYRLvDz+Wz1s6wi8
         */

        private String emp_name;
        private String emp_id;
        private String photo;

        public String getEmp_name() {
            return emp_name;
        }

        public void setEmp_name(String emp_name) {
            this.emp_name = emp_name;
        }

        public String getEmp_id() {
            return emp_id;
        }

        public void setEmp_id(String emp_id) {
            this.emp_id = emp_id;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }
}

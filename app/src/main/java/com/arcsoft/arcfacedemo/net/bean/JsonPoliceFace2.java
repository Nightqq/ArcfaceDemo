package com.arcsoft.arcfacedemo.net.bean;

import java.util.List;

public class JsonPoliceFace2 {
    /**
     * success : true
     * source : {"data":[{"emp_id":"143136","emp_name":"郑晨","emp_type":0,"emp_feature":"AAD6RAAAdEMBHoC8NpisPOnBB73H8Y683+coumoJUTwhUjw9MGUoPUcNj70vtIy8s17RvSp9S7aV8c+9xwaOPam/Db0Nxtk7f7y3vUw7BT6byI+8oDEBPecb4DzRmDS9Qxpsvc2eNr4H2wO9xAyjvXwphD3Wvcw8BDskPfOyV71Mvss8rVmXvRnPc70KfOK9Dr20PadHFbwGtH29HBVqvIWQnr3yIQu5gNIQvWFx+LwFB6w93F6kPXm+j72omkQ9Q/SmPHaY8z1V9NE7ucAQPcPi7D3/7Uy9MVYMvR+eCz2/sbQ7t3vsPdxNNj1bJMa88QHzvF9Bmj1qsFm9/FCzPX7Mr722PsE9lWMRvb/xqLyWjsm9QaaQvcr+Rb02OCi8jQtWPduCTjzAy7c9KytIvcYkfr1zOA09vF/fvEz5IT1K95Q8gE5jvcFhfL0a74Y8BFGdvWooE7zZRV85qO7zu58FdD0JIwA+whwWvocsHz1WK4o8hgVDveh4HzxKwZ68+nNQO8MEhD0Vtb+8sYXNuu3N7zwkL+S9c00CvXfGWT3CPzu9hOPDPPQel71TATg+HLgHPhnNvjxE22+8DDVfPYoEfj1EP4+9nACgvWC0uLvBO4G8itBAPb4EjzqNSo2807XSPV2RIjy+OjA8g1UovI1qaD2YB6S81FqHPd41gj1pnDy8XHuAPY2pZD1Bn1W9szEGPZA+s72fO0O9uq2aPCEfgT0i+r48g+JIvc8jcjwuK+k9WTWIvYn+/DwEdg+9vN96vR0Wqj3eHXK9RohwPBm53TyEtJM9NEKivWKnfjsASE89DZUSPOmvpj2ZNLK9KPgxPWL4AD0ZGks9KmqpPckqiDy+VLi8TANzvE+UFb1lbeY8uZaNPaoVnD0DREy9WYeRPe66Lb1x7869EvP0vM9RMD0c3Tu8djsavl77AL2kThk8+oK+O227Gz0ni2g9DdoHvhbNdT2npAw8JX7rvJreFr6HIxU9bF22PSEkBD4GBq28WxdqPSIMXz1B3Yi9Rk/fPOrSwTwHuKA9tgONvWCAKT0D8Ae7sU8+vHpSGbz2PFY9NxgCO2usc7x/BKa8ijKKuzeOaLwHtAS8ocmAPQQ5L7zzjym9phiwPcpdoL3J/5i7tha+PfZ6iD0Q1Z87fdAfPQtuEj1Evdg8P5PjvDM1uLzi/UO9XBTqPXO0LryZJAC+nKRvO89ouTo1O7y9ULCvvdM01LuMSDU9jj+TPfbhpj1hJo6930CuPbmAODzK+QU+EhxMvXPdKT2GY3g937yGvSLXCDzUsgI+R2UKPqUv+r2QXwO85SijvU8Rdzy6B+u7a/iAPFy4Qby+8IM7EP4sPokk0T2P8nI92ZMCvb0nZ70YSYa9","valid":0,"sfz":"143136","tqsj":"2020-08-17","tqzp":null},{"emp_id":"14797D","emp_name":"职工1","emp_type":0,"emp_feature":"AAD6RAAAdEPb8U6980I2PSURT70l/BU923FIPezNjrxpaIA7xSnKvJwazb3aaXo7fO/TvYjCRDt0H3a9rUuNPezYMr1FxSE9gSXfvd3DBD5JvY47kQ2WPUwalz0NI769sC2FvQb8CL7D0R07mo1FvVlIhD2e6DA8xQSaPY56KzxSUOQ86/mRvQyMgr3yk7W9yarxPHojfzzvImq7uLi5OTsIDL1ZkJy9u5OYvcQ3nr3Yz6c9iHi/Pdspw72pjp081J+avZcP9j0vRc+8qBTGPJ5xuj28qsy92v3xvGpX1zyAVhy9fXIbPnrzdzrvhEq8uaPMPGHGhD3oefi8VvbTOzuWSr3WmxU9FkWuvIQllT3eMLy96zjSvVzcyb2M0Km8NkcUPngTUD1FEho9Ke4JvUxNhb0eseM9NG+zvbqnrjttTj89KvwnvRKIsb0tFJM81l7jvULYAbx0ZnS9wAJtu6T8iT2n6R09Y/q/vfxBgT3Camk7hrp8vX43gbfYSCm8+n0zvU+ZWryvKsy8UyIuvYqzlT0Kvdm9cjewO3eQ0jzsJR29HCVMu0jbzr1lbyo+2KelPV9nAzuuU5M89a3iPCmCwTpf/nW8JfGHvd5Uez02OrK8I2X0O1vsOTwfRzC9FG2QPRi3Pz3w4zW7PkUzPJXHrD2g04i7dzDFPA7uRj35g2o8+tq+PHDyBj0m29q8DX/4PaDFrL1oUaS9vgo+PWqU6T0fUnO9YRcsvYnqtrxELHY8kCFOvUbin7t1r/W7BSIrPcWR7j0G9YK9blcGPTT3DTz8sug9eq5SvVa7F73w9K49wVQdPWNbSD1LUBi9oZSkPRgL8jwAwR09hqvHPVjTgj1FCuQ8f9kJPaALVjzOb5M8AeYEPbEAdz0LAIs85giGPRVl67xYWt29Mmp1vDIdcTwtn+28ZsUEvimaQ735Qss8lhkdvSbcijwGWam8ZTYGvmAi4T0IIgo93FcePJayN74Ke2W8l16hPQ384z1R0gS8QMs8PNYKVj1FnJG9cJjZPEvLXr1gPL08u7iRvWL3Lz069To9lhRFvI9dB72cjQY9ZEy2vK2i0Tu+Y8087rMnPYyFzLzESgY9+FShPauVojtjrti9pAqaPR6Amr1UWxC9c5orPXsRoz3WFsg87rg+PZfUBb3Uz227JcO5PHPXDr2+4IG9idPXPa1eY710aLK9Z2Z6PcUXALyudIu9WBmwvQm6C72jepQ8Hjm5Pe4Rvz1CDVi8nC+5PTZRMj3dKwo+T+kJvZLrqrsD0bc9wvAcveAPKz1S/po9rNsqPtIomL32vP08HKq4vbLD9buasL48aUFvvUDnRj2VcZ687lbAPVMCwj0oixY9f//uvD0MO7yBHQy9","valid":0,"sfz":"320106197803051233","tqsj":"2020-04-10","tqzp":null}]}
     */

    private boolean success;
    private SourceBean source;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public SourceBean getSource() {
        return source;
    }

    public void setSource(SourceBean source) {
        this.source = source;
    }

    public static class SourceBean {
        private List<DataBean> data;

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * emp_id : 143136
             * emp_name : 郑晨
             * emp_type : 0
             * emp_feature : AAD6RAAAdEMBHoC8NpisPOnBB73H8Y683+coumoJUTwhUjw9MGUoPUcNj70vtIy8s17RvSp9S7aV8c+9xwaOPam/Db0Nxtk7f7y3vUw7BT6byI+8oDEBPecb4DzRmDS9Qxpsvc2eNr4H2wO9xAyjvXwphD3Wvcw8BDskPfOyV71Mvss8rVmXvRnPc70KfOK9Dr20PadHFbwGtH29HBVqvIWQnr3yIQu5gNIQvWFx+LwFB6w93F6kPXm+j72omkQ9Q/SmPHaY8z1V9NE7ucAQPcPi7D3/7Uy9MVYMvR+eCz2/sbQ7t3vsPdxNNj1bJMa88QHzvF9Bmj1qsFm9/FCzPX7Mr722PsE9lWMRvb/xqLyWjsm9QaaQvcr+Rb02OCi8jQtWPduCTjzAy7c9KytIvcYkfr1zOA09vF/fvEz5IT1K95Q8gE5jvcFhfL0a74Y8BFGdvWooE7zZRV85qO7zu58FdD0JIwA+whwWvocsHz1WK4o8hgVDveh4HzxKwZ68+nNQO8MEhD0Vtb+8sYXNuu3N7zwkL+S9c00CvXfGWT3CPzu9hOPDPPQel71TATg+HLgHPhnNvjxE22+8DDVfPYoEfj1EP4+9nACgvWC0uLvBO4G8itBAPb4EjzqNSo2807XSPV2RIjy+OjA8g1UovI1qaD2YB6S81FqHPd41gj1pnDy8XHuAPY2pZD1Bn1W9szEGPZA+s72fO0O9uq2aPCEfgT0i+r48g+JIvc8jcjwuK+k9WTWIvYn+/DwEdg+9vN96vR0Wqj3eHXK9RohwPBm53TyEtJM9NEKivWKnfjsASE89DZUSPOmvpj2ZNLK9KPgxPWL4AD0ZGks9KmqpPckqiDy+VLi8TANzvE+UFb1lbeY8uZaNPaoVnD0DREy9WYeRPe66Lb1x7869EvP0vM9RMD0c3Tu8djsavl77AL2kThk8+oK+O227Gz0ni2g9DdoHvhbNdT2npAw8JX7rvJreFr6HIxU9bF22PSEkBD4GBq28WxdqPSIMXz1B3Yi9Rk/fPOrSwTwHuKA9tgONvWCAKT0D8Ae7sU8+vHpSGbz2PFY9NxgCO2usc7x/BKa8ijKKuzeOaLwHtAS8ocmAPQQ5L7zzjym9phiwPcpdoL3J/5i7tha+PfZ6iD0Q1Z87fdAfPQtuEj1Evdg8P5PjvDM1uLzi/UO9XBTqPXO0LryZJAC+nKRvO89ouTo1O7y9ULCvvdM01LuMSDU9jj+TPfbhpj1hJo6930CuPbmAODzK+QU+EhxMvXPdKT2GY3g937yGvSLXCDzUsgI+R2UKPqUv+r2QXwO85SijvU8Rdzy6B+u7a/iAPFy4Qby+8IM7EP4sPokk0T2P8nI92ZMCvb0nZ70YSYa9
             * valid : 0
             * sfz : 143136
             * tqsj : 2020-08-17
             * tqzp : null
             */

            private String emp_id;
            private String emp_name;
            private int emp_type;
            private String emp_feature;
            private int valid;
            private String sfz;
            private String tqsj;
            private Object tqzp;

            public String getEmp_id() {
                return emp_id;
            }

            public void setEmp_id(String emp_id) {
                this.emp_id = emp_id;
            }

            public String getEmp_name() {
                return emp_name;
            }

            public void setEmp_name(String emp_name) {
                this.emp_name = emp_name;
            }

            public int getEmp_type() {
                return emp_type;
            }

            public void setEmp_type(int emp_type) {
                this.emp_type = emp_type;
            }

            public String getEmp_feature() {
                return emp_feature;
            }

            public void setEmp_feature(String emp_feature) {
                this.emp_feature = emp_feature;
            }

            public int getValid() {
                return valid;
            }

            public void setValid(int valid) {
                this.valid = valid;
            }

            public String getSfz() {
                return sfz;
            }

            public void setSfz(String sfz) {
                this.sfz = sfz;
            }

            public String getTqsj() {
                return tqsj;
            }

            public void setTqsj(String tqsj) {
                this.tqsj = tqsj;
            }

            public Object getTqzp() {
                return tqzp;
            }

            public void setTqzp(Object tqzp) {
                this.tqzp = tqzp;
            }
        }
    }
}

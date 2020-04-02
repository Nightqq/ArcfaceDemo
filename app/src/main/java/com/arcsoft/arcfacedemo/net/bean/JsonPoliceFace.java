package com.arcsoft.arcfacedemo.net.bean;

import com.arcsoft.arcfacedemo.dao.bean.PoliceFace;

import java.util.List;

public class JsonPoliceFace {


    /**
     * success : true
     * source : {"data":[{"emp_id":"2859CD              ","emp_name":"翟佟","emp_type":0,"emp_feature":"AAD6RAAAdEPkwwW97kkCPu1CWD3kmrE8H/wJvRra3btL0sQ6IYpHu2oBU71b2NC9M/MUPX1vLrzF0hG9lqwHPWv6QLytnjq9JyS/vX8U5zwX2ZU88HTgPJltOz3/GWE8ofqNvKkqrL3db4c976UJPSB9nr3HUDe9iwVzPAPFm7wpgOo7pik1PapXNL2sZo48s4vFvZV0CL5q4T49fir6PAQymT2MQsS8e5CMPbDzjr1eiAM9Kk0HvEsAvryg1hq9j2KpPeRR7zzZ3Xk9cythvU2opD3o37W9f/0SPZTkFDkkfIi9CtXwPJOiBDyIpWC9gXy3vWqfhD2cKQI9VeaSPblombsDJwM9v8goPdYaej3uxg69slR/PaiQjb0pYAK+mkXQPEEV0T2NnLY9k3c4variQzy00+w9N5ghPa+2cj3qboQ949nSvRgYYr1aHwq+Rw8OvfeuHr1/EFY9NwExPAjnxL1JCMg84HGXPbgTlLwfeiU7L7EnvVn8kr3j6SY9GVXmvJyekDzwTiK8adWBPXZju7y/IO+8aitGvVWwTjuth4C9ym0APozBYbvC8hU798CePYiwjL0Kqvu8lYPNPJjx4T22IE+9l7B6vZXx570MuYy9TnJiPTq+0TyJAQW8d+K3PfJw1ryQGMe7Mn1fveztkz1KRNu9AhQUPdojo7w6F5K8NWFkPZhMrjzKic28IIOZPUKyfj0WSxi6uo3MvYGynjx/8QQ+KgBjvSVjpz0kEVE9FoSXvTRj0brDKGQ62ZsIvQ03Ojxushy8C0LAvVg9JDyKrk692qxhvFAWqL0gEhA+t5R1vaxgs72xMYA9Uea5Pe14obwjgy69IdCguLvGwrvy1w68fvaZvbpbEb2BEvW7F5O/PO8Vz73o11K9Mw19PBs0TT3iYvm9D58uPc0ViTwiFtu9dXWLvfc8rzwTVK29Mh0mvVOUST0Q7vy9SajeO2JKrj21x6C9tAuaPVTZAj6j8Z04i8AsvXTC+D37IaE9RxQYvUB8Wj37Z0c983cTPkS4pLyhY/G9wA1JvSXyjr32L6i9FuuKO0ePirxmtu+76sitPdljcz19TDA8BnEIvSgxMb08Af28PazBvdjcOj1+XYI8cXUGPSjeuD34VZw9CmGWPG+QPr060Ig9uixxPF3utD3IXJs9T3xWvbudKz1J1uC8cr8xO9n/CTww/rK9LD4Uu/Pytb10jDA9+EGhvKb+db0UGLm9x1iPPWB2tj3VNk89XA0zOpXbQT5Ozq29BXtzvdhA+b0dRgE9mq3LvDdTzTwpXq27mhBMPTURf71xwrI8/a+0PAE1Dz6ua4894LMiPs3NrTnEAKQ9255CPQt8bT0daCa8ZNe8PABfAb1iZjW8","valid":0,"sfz":"2859CD","tqsj":"2020-03-19","tqzp":null},{"emp_id":"2859CD              ","emp_name":"翟佟","emp_type":0,"emp_feature":"AAD6RAAAdEPkwwW97kkCPu1CWD3kmrE8H/wJvRra3btL0sQ6IYpHu2oBU71b2NC9M/MUPX1vLrzF0hG9lqwHPWv6QLytnjq9JyS/vX8U5zwX2ZU88HTgPJltOz3/GWE8ofqNvKkqrL3db4c976UJPSB9nr3HUDe9iwVzPAPFm7wpgOo7pik1PapXNL2sZo48s4vFvZV0CL5q4T49fir6PAQymT2MQsS8e5CMPbDzjr1eiAM9Kk0HvEsAvryg1hq9j2KpPeRR7zzZ3Xk9cythvU2opD3o37W9f/0SPZTkFDkkfIi9CtXwPJOiBDyIpWC9gXy3vWqfhD2cKQI9VeaSPblombsDJwM9v8goPdYaej3uxg69slR/PaiQjb0pYAK+mkXQPEEV0T2NnLY9k3c4variQzy00+w9N5ghPa+2cj3qboQ949nSvRgYYr1aHwq+Rw8OvfeuHr1/EFY9NwExPAjnxL1JCMg84HGXPbgTlLwfeiU7L7EnvVn8kr3j6SY9GVXmvJyekDzwTiK8adWBPXZju7y/IO+8aitGvVWwTjuth4C9ym0APozBYbvC8hU798CePYiwjL0Kqvu8lYPNPJjx4T22IE+9l7B6vZXx570MuYy9TnJiPTq+0TyJAQW8d+K3PfJw1ryQGMe7Mn1fveztkz1KRNu9AhQUPdojo7w6F5K8NWFkPZhMrjzKic28IIOZPUKyfj0WSxi6uo3MvYGynjx/8QQ+KgBjvSVjpz0kEVE9FoSXvTRj0brDKGQ62ZsIvQ03Ojxushy8C0LAvVg9JDyKrk692qxhvFAWqL0gEhA+t5R1vaxgs72xMYA9Uea5Pe14obwjgy69IdCguLvGwrvy1w68fvaZvbpbEb2BEvW7F5O/PO8Vz73o11K9Mw19PBs0TT3iYvm9D58uPc0ViTwiFtu9dXWLvfc8rzwTVK29Mh0mvVOUST0Q7vy9SajeO2JKrj21x6C9tAuaPVTZAj6j8Z04i8AsvXTC+D37IaE9RxQYvUB8Wj37Z0c983cTPkS4pLyhY/G9wA1JvSXyjr32L6i9FuuKO0ePirxmtu+76sitPdljcz19TDA8BnEIvSgxMb08Af28PazBvdjcOj1+XYI8cXUGPSjeuD34VZw9CmGWPG+QPr060Ig9uixxPF3utD3IXJs9T3xWvbudKz1J1uC8cr8xO9n/CTww/rK9LD4Uu/Pytb10jDA9+EGhvKb+db0UGLm9x1iPPWB2tj3VNk89XA0zOpXbQT5Ozq29BXtzvdhA+b0dRgE9mq3LvDdTzTwpXq27mhBMPTURf71xwrI8/a+0PAE1Dz6ua4894LMiPs3NrTnEAKQ9255CPQt8bT0daCa8ZNe8PABfAb1iZjW8","valid":0,"sfz":"2859CD","tqsj":"2020-03-19","tqzp":null}]}
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
        private List<PoliceFace> data;

        public List<PoliceFace> getData() {
            return data;
        }

        public void setData(List<PoliceFace> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * emp_id : 2859CD
             * emp_name : 翟佟
             * emp_type : 0
             * emp_feature : AAD6RAAAdEPkwwW97kkCPu1CWD3kmrE8H/wJvRra3btL0sQ6IYpHu2oBU71b2NC9M/MUPX1vLrzF0hG9lqwHPWv6QLytnjq9JyS/vX8U5zwX2ZU88HTgPJltOz3/GWE8ofqNvKkqrL3db4c976UJPSB9nr3HUDe9iwVzPAPFm7wpgOo7pik1PapXNL2sZo48s4vFvZV0CL5q4T49fir6PAQymT2MQsS8e5CMPbDzjr1eiAM9Kk0HvEsAvryg1hq9j2KpPeRR7zzZ3Xk9cythvU2opD3o37W9f/0SPZTkFDkkfIi9CtXwPJOiBDyIpWC9gXy3vWqfhD2cKQI9VeaSPblombsDJwM9v8goPdYaej3uxg69slR/PaiQjb0pYAK+mkXQPEEV0T2NnLY9k3c4variQzy00+w9N5ghPa+2cj3qboQ949nSvRgYYr1aHwq+Rw8OvfeuHr1/EFY9NwExPAjnxL1JCMg84HGXPbgTlLwfeiU7L7EnvVn8kr3j6SY9GVXmvJyekDzwTiK8adWBPXZju7y/IO+8aitGvVWwTjuth4C9ym0APozBYbvC8hU798CePYiwjL0Kqvu8lYPNPJjx4T22IE+9l7B6vZXx570MuYy9TnJiPTq+0TyJAQW8d+K3PfJw1ryQGMe7Mn1fveztkz1KRNu9AhQUPdojo7w6F5K8NWFkPZhMrjzKic28IIOZPUKyfj0WSxi6uo3MvYGynjx/8QQ+KgBjvSVjpz0kEVE9FoSXvTRj0brDKGQ62ZsIvQ03Ojxushy8C0LAvVg9JDyKrk692qxhvFAWqL0gEhA+t5R1vaxgs72xMYA9Uea5Pe14obwjgy69IdCguLvGwrvy1w68fvaZvbpbEb2BEvW7F5O/PO8Vz73o11K9Mw19PBs0TT3iYvm9D58uPc0ViTwiFtu9dXWLvfc8rzwTVK29Mh0mvVOUST0Q7vy9SajeO2JKrj21x6C9tAuaPVTZAj6j8Z04i8AsvXTC+D37IaE9RxQYvUB8Wj37Z0c983cTPkS4pLyhY/G9wA1JvSXyjr32L6i9FuuKO0ePirxmtu+76sitPdljcz19TDA8BnEIvSgxMb08Af28PazBvdjcOj1+XYI8cXUGPSjeuD34VZw9CmGWPG+QPr060Ig9uixxPF3utD3IXJs9T3xWvbudKz1J1uC8cr8xO9n/CTww/rK9LD4Uu/Pytb10jDA9+EGhvKb+db0UGLm9x1iPPWB2tj3VNk89XA0zOpXbQT5Ozq29BXtzvdhA+b0dRgE9mq3LvDdTzTwpXq27mhBMPTURf71xwrI8/a+0PAE1Dz6ua4894LMiPs3NrTnEAKQ9255CPQt8bT0daCa8ZNe8PABfAb1iZjW8
             * valid : 0
             * sfz : 2859CD
             * tqsj : 2020-03-19
             * tqzp : null
             */

            private String emp_id;
            private String emp_name;
            private int emp_type;
            private String emp_feature;
            private int valid;
            private String sfz;
            private String tqsj;
            private String tqzp;

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

            public void setTqzp(String tqzp) {
                this.tqzp = tqzp;
            }
        }
    }

    @Override
    public String toString() {
        return "JsonPoliceFace{" +
                "success=" + success +
                ", source=" + source +
                '}';
    }
}

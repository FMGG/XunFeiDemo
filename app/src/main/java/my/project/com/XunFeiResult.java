package my.project.com;

import java.util.List;

public class XunFeiResult {


    /**
     * sn : 1
     * ls : true
     * bg : 0
     * ed : 0
     * ws : [{"bg":0,"slot":"WFST","cw":[{"sc":0,"w":"你好你好。"}]}]
     * sc : 0
     */

    private int sn;
    private boolean ls;
    private int bg;
    private int ed;
    private int sc;
    private List<WsBean> ws;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean isLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public int getSc() {
        return sc;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }

    public List<WsBean> getWs() {
        return ws;
    }

    public void setWs(List<WsBean> ws) {
        this.ws = ws;
    }

    public static class WsBean {
        /**
         * bg : 0
         * slot : WFST
         * cw : [{"sc":0,"w":"你好你好。"}]
         */

        private int bg;
        private String slot;
        private List<CwBean> cw;

        public int getBg() {
            return bg;
        }

        public void setBg(int bg) {
            this.bg = bg;
        }

        public String getSlot() {
            return slot;
        }

        public void setSlot(String slot) {
            this.slot = slot;
        }

        public List<CwBean> getCw() {
            return cw;
        }

        public void setCw(List<CwBean> cw) {
            this.cw = cw;
        }

        public static class CwBean {
            /**
             * sc : 0
             * w : 你好你好。
             */

            private int sc;
            private String w;

            public int getSc() {
                return sc;
            }

            public void setSc(int sc) {
                this.sc = sc;
            }

            public String getW() {
                return w;
            }

            public void setW(String w) {
                this.w = w;
            }
        }
    }
}

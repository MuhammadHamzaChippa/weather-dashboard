(ns dashboard.highcharts
  (:require ["highcharts/highstock" :as HighCharts]
            [dashboard.helper :refer [extract-detail convert-month date-to-time condition-to-image-url ]]))


(defn weather-forcast-chart 
  "config for the weather forecast chart"
  [data] 
  (let [weather-details (mapv (fn [x] {  
                            :sunrise (js/Date. (js/parseInt (str (:sunrise x) "000"))) ; parse the date to js/Date object
                            :sunset (js/Date. (js/parseInt (str (:sunset x) "000")))
                            :pressure (:pressure x) 
                            :wind_speed (:wind_speed x)
                            :humidity (:humidity x)
                            :max (:max (:temp  x))
                            :clouds (:clouds x)
                            :weather (:main (first (:weather x)))}) data)
        dates (map (fn [x] (js/Date. (js/parseInt (str (:dt x) "000")))) data)
        formatted-dates (map (fn [date] (str (convert-month (.getMonth date))" "(.getDate date) ", "(.getFullYear date))) dates)
        max-data (mapv (fn [x] (:max (:temp x)) ) data)
        min-data (mapv (fn [x] (:min (:temp x))) data)]
    
    {:title {:text "Weather Forecast" :style {:color "white"}}
     :yAxis {
             :title {:text "Temperature (°C)" :style {:color "white"}}
             :labels {:style {:color "white"}
                      :formatter (fn [] (this-as this (str (.-value this) "°C")))}}
     
     :legend {:borderRadius "10px"
              :borderWidth 1
              :itemStyle {:color "white"}}
     
     :plotOptions {:series {:dataGrouping   {:enabled false}
                            :turboThreshold 0}}
     :xAxis       {
                   :categories formatted-dates
                   :labels     {:style {:color "white"}
                                :formatter (fn []
                                             (this-as this
                                                      (.-value this)))}}
     :chart {
             :borderRadius "10px"
             :backgroundColor "#313E48"
             :style {:color "white"}}

     :tooltip {:shared true
               :crosshairs true
               :useHTML true
               :formatter (fn [] (this-as this (let [max-series (first (.-points this))
                                                     min-series  (second (.-points this))
                                                     ]
                    (str 
                    "<div class=\"tool weather\">
                    <div class=\"icon bubble black\">
                        <div class=\"spin\">
                            <img src=\"" (condition-to-image-url (extract-detail weather-details "weather" (.-y max-series))) "\">
                        </div>
                    </div>
                    <div class=\"tooltip-value-container\">
                    <h3 class = \"tooltip-value\"><b>Date: </b>" (.-x max-series) "</h3>
                    <h3 class = \"tooltip-value\"><b>Condition: </b>" (extract-detail weather-details "weather" (.-y max-series)) "</h3>
                    <h3 class = \"tooltip-value\"><b>Max Temp: </b>" (.-y max-series) "°C</h3>
                    <h3 class = \"tooltip-value\"><b>Min Temp: </b>" (.-y min-series) "°C</h3>
                    <h3 class = \"tooltip-value\"><b>Sunrise Time: </b>" (date-to-time (extract-detail weather-details "sunrise" (.-y max-series))) "</h3>
                    <h3 class = \"tooltip-value\"><b>Sunset Time: </b>" (date-to-time (extract-detail weather-details "sunset" (.-y max-series))) "</h3>                                                                                                                                 
                    <h3 class = \"tooltip-value\"><b>Pressure: </b>" (extract-detail weather-details "pressure" (.-y max-series)) " hPa</h3>
                    <h3 class = \"tooltip-value\"><b>Humidity: </b>" (extract-detail weather-details "humidity" (.-y max-series)) "%</h3>
                    <h3 class = \"tooltip-value\"><b>Wind Speed: </b>" (extract-detail weather-details "wind_speed" (.-y max-series)) " metre/sec</h3>
                    <h3 class = \"tooltip-value\"><b>Cloudiness: </b>" (extract-detail weather-details "clouds" (.-y max-series)) "%</h3>                                                                                                               

                                                                                                                                       <div>
                </div>"))))

               :backgroundColor  "#708090"
               :borderColor  "#708090"
               :borderRadius 10
               :borderWidth 3
               }
              
     
     :series [{:name "Max Temperature"
               :data max-data
               :color "#7FFFFF"}
              {:name "Min Temperature"
               :data min-data
               :color "#FF6666"}]}))

(defn render-chart []
  (let [div (atom nil)]
    (fn [chart-config data]
      [:div {:style {:width "100%"}
             :ref (fn [el]
                    (when-not (nil? el)
                      (reset! div el)
                      (-> (HighCharts/Chart. el (clj->js (chart-config data)))
                          (.reflow))))}])))

;; (defn render [chart-config]
;;   [render-chart chart-config])

;; (defn render-line-chart []
;;   (render line-chart))

(defn render [chart-config data]
  (when-not (empty? data)
    [render-chart chart-config data]))
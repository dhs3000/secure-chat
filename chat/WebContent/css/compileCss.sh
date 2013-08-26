lessc styles.less  > styles-min-intermediate.css

#java -jar css-utils-1.0.jar < styles-min-intermediate.css > styles-min-$(date -d "today" +"%Y-%m-%d-%H-%M").css
java -jar css-utils-1.0.jar < styles-min-intermediate.css > styles-min.css

rm styles-min-intermediate.css




library(dplyr)

allvect = c(
  "Austria",
  "Belgium",
  "Bulgaria",
  "Croatia",
  "Czech Republic",
  "Denmark",
  "Estonia",
  "Finland",
  "France",
  "Germany",
  "Greece",
  "Hungary",
  "Ireland",
  "Italy",
  "Latvia",
  "Lithuania",
  "Poland",
  "Portugal",
  "Romania",
  "Slovenia",
  "Sweden",
  "United Kingdom",
  "Netherlands",
  "Slovakia"
)



data <- data_proxy
countries <- data %>% select(Country) %>% group_by(Country) %>% summarise(n=n()) %>% arrange(Country)

products <- data %>% select(Country, Product.query) %>% group_by(Country, Product.query) %>% summarise(n=n()) %>% arrange(Country)

resvect = c(sort(countries[,1]$Country))

# test names
setdiff(resvect, allvect) #should be empty

# test result
setdiff(allvect, resvect)

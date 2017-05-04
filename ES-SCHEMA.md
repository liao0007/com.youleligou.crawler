```
PUT eleme-restaurant
{
  "mappings": {
      "latest": {
        "properties": {
          "address": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "id": {
            "type": "long"
          },
          "identification": {
            "properties": {
              "companyName": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "licensesNumber": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              }
            }
          },
          "location": {
            "type": "geo_point"
          },
          "name": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          }
        }
      }
    },
    "settings": {
      "index": {
        "number_of_shards": "3",
        "number_of_replicas": "1"
      }
    }
}
```

```
PUT eleme-food
{
  "mappings": {
      "snapshot": {
        "properties": {
          "category": {
            "properties": {
              "activity": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "description": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "iconUrl": {
                "type": "text"
              },
              "id": {
                "type": "long"
              },
              "isActivity": {
                "type": "boolean"
              },
              "name": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "typ": {
                "type": "integer"
              }
            }
          },
          "createdAt": {
            "type": "date"
          },
          "createdDate": {
            "type": "date"
          },
          "description": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "id": {
            "type": "text"
          },
          "itemId": {
            "type": "long"
          },
          "monthSales": {
            "type": "integer"
          },
          "name": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "rating": {
            "type": "float"
          },
          "ratingCount": {
            "type": "integer"
          },
          "restaurant": {
            "properties": {
              "address": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "id": {
                "type": "long"
              },
              "identification": {
                "properties": {
                  "companyName": {
                    "type": "text",
                    "fields": {
                      "keyword": {
                        "type": "keyword",
                        "ignore_above": 256
                      }
                    }
                  },
                  "licensesNumber": {
                    "type": "text",
                    "fields": {
                      "keyword": {
                        "type": "keyword",
                        "ignore_above": 256
                      }
                    }
                  }
                }
              },
              "location": {
                "type": "geo_point"
              },
              "name": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              }
            }
          },
          "satisfyCount": {
            "type": "integer"
          },
          "satisfyRate": {
            "type": "float"
          }
        }
      }
    },
    "settings": {
      "index": {
        "number_of_shards": "3",
        "number_of_replicas": "1"
      }
    }
}
```
```
PUT eleme
{
  "eleme": {
    "aliases": {},
    "mappings": {
      "food_snapshot": {
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
      }
    },
    "settings": {
      "index": {
        "creation_date": "1493883793324",
        "number_of_shards": "3",
        "number_of_replicas": "1",
        "uuid": "whOgEqEfQhq7RtNxupT2tg",
        "version": {
          "created": "5030199"
        },
        "provided_name": "eleme"
      }
    }
  }
}
```
package com.datn.viettel.common;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Constants {
    private Constants() {} // Không cho tạo đoi tượng từ lớp này. Nục đích để lưu các hằng số chung


    public static class Common {
        public static final String APPLICATION_CODE = "CHATBOT_AI";
        public static final String SYSTEM_PROMPT = """
                Your task is to answer customer questions strictly based on the provided product, service, and policy information.
                Do not speculate, infer, or introduce information that is not explicitly included.
                If a customer asks about something not covered, respond in a neutral manner and encourage them to contact a sales representative
                for further assistance. For greetings and polite expressions (e.g., 'hello', 'thank you'), respond in a friendly and professional way.
                Keep your answers Short, clear, and focused on guiding the customer toward their purchase needs, while maintaining a professional and approachable tone.""";

        private Common() {
        }
    }
    public static final class Database {
        private Database() {}

        public static final class Primary{
            private Primary () {}

            public static final String BEAN_PRIMARY_DATASOURCE = "primaryDataSource";
            public static final String PROPERTY_PREFIX = "spring.datasource";
            public static final String PACKAGE_REPO = "com.datn.viettel.repositories.core";
            public static final String BEAN_ENTITY_MANAGER_FACTORY = "entityManagerFactory";
            public static final String PACKAGE_ENTITY = "com.datn.viettel.entities.core";
            public static final String UNIT = "primary_datasource";
            public static final String BEAN_TRANSACTION_MANAGER = "transactionManager";
        }
    }

    public static class Conversation {
        private Conversation() {
        }

        public static class Role {
            public static final String ASSISTANT = "ASSISTANT";
            public static final String SYSTEM = "SYSTEM";
            public static final String TOOL = "TOOL";
            public static final String USER = "USER";

            private Role() {
            }
        }
    }

    public class VectorType {
        private VectorType() {}
        public static final String MOBILE_PACKAGE = "MOBILE_PACKAGE";
        public static final String FTTH_PACKAGE = "FTTH_PACKAGE";
        public static final String SIM = "SIM";
    }

    public static final class ModelAI {
        private ModelAI() {}

        public static final String GEMINI = "gemini";
    }

    public static final class Status {
        private Status() {}

        public static final short ACTIVE = 1;
        public static final short INACTIVE = 0;
        public static final String ACTIVE_STR = "1";
        public static final String INACTIVE_STR = "0";
        public static final class Register {
            private Register() {}

            public static final Short PROCESSING = 1;
            public static final Short COMPLETED = 2;
            public static final Short FAILED = 3;
        }
    }

    public static class RegisterType {
        private RegisterType() {
        }

        public static final Short REGISTER = 1;
        public static final Short GIFT = 2;
    }
    public static class PaymentType {
        private PaymentType() {
        }

        public static final short DIRECT = 0;
        public static final Short QR = 1;
    }

    public static class Language {
        public static final String VI = "vi";
        public static final String EN = "en";
        public static final String VIE = "vie";
        public static final String ENG = "eng";
        public static final String ALL = "all";
        public static final String FOLDER = "templates/";
        public static final String RS_VI = "vi/";
        public static final String RS_EN = "en/";
        private static final String[] VALUES_ARRAY = {VI, EN, VIE, ENG};
        private static final List<Locale> LOCALES = Arrays.asList(new Locale(VI), new Locale(EN));

        private Language() {
        }

        public static List<String> toList() {
            return Arrays.asList(VALUES_ARRAY);
        }

        public static List<Locale> getLocales() {
            return LOCALES;
        }
    }

    public static class ServiceType {
        private ServiceType() {
        }

        public static final Short MOBILE_PACKAGE = 0;
        public static final Short PHONE_DEVICE = 1;
        public static final Short FTTH_PACKAGE = 2;
        public static final Short SIM = 3;

        public static final Map<Short, String> typeName = Map.of(
                PHONE_DEVICE, "Handset",
                FTTH_PACKAGE, "Service",
                SIM, "SIM"
        );
    }

    public static class ExecutionCode {
        public static final String SUCCESS = "0";
        public static final String ERROR = "1";
        public static final String INTEGRATION_ERROR = "2";
        public static final String BUSINESS_ERROR = "3";

        private ExecutionCode() {
        }
    }

    public static class AIConfig {
        private AIConfig() {};

        public static final String LAST_MESSAGE_PROMPT_DEFAULT = "5";
        public static final String VECTOR_TOP_DEFAULT = "5";
        public static final String VECTOR_SCORE_DEFAULT = "1.7";
        public static final String LAST_MESSAGE_KNOWLEDGE_DEFAULT = "7";
    }

    public static class SystemConfig {
        private SystemConfig() {
        }

        public static final String SYSTEM_PROMPT_MOBILE_PACKAGE = "SYSTEM_PROMPT_MOBILE_PACKAGE";
        public static final String SYSTEM_PROMPT_FTTH_PACKAGE = "SYSTEM_PROMPT_FTTH_PACKAGE";
        public static final String SYSTEM_PROMPT_SIM = "SYSTEM_PROMPT_SIM";
    }

    public static class OrderStatus {
        private OrderStatus() {
        }

        public static final short PENDING = 0;
        public static final short SUCCESS = 1;
        public static final short FAILED = 2;
        public static final short CANCELLED = 3;
    }

    public static class ProductType {
        private ProductType() {}

        public static final Short HANDSET = 0;
        public static final Short SERVICE = 1;
        public static final Short SIM = 2;
    }


    public static class SystemPromptQuery {
        private SystemPromptQuery() {
        }

        public static final String MOBILE_PACKAGE = """
            CRITICAL JSON VALIDATION: Before responding, count opening { and closing } braces - they MUST be equal. Also count opening [ and closing ] brackets - they MUST be equal.
            CRITICAL CONSTRAINT: You MUST NEVER use any predefined data, sample data, default values, or placeholder information. Only work with data explicitly provided by the user in their current request.
            STRICTLY FORBIDDEN:
                Adding any data not directly given by the user
                Using examples from training data or previous conversations
                Including default filters, conditions, or parameters
                Referencing any sample datasets or template values
                NEVER use sample JSON structures like range queries, filters, or any JSON examples unless the user explicitly provides that exact JSON in their request
            JSON STRUCTURE CONSTRAINT: Parse user queries to extract data and generate JSON queries using provided formats as STRUCTURE REFERENCE ONLY. The sample JSON formats are solely for understanding the required structure - DO NOT use any field names, values, conditions, or parameters from the samples unless explicitly provided by the user.
            REQUIRED: If user provides no data, respond "///".
                {
                      "bool": {
                        "filter": [
                          {
                            "range": {
                              "content.money_fee": {
                                "gte": 8000,
                                "lte": 12000
                              }
                            }
                          }
                        ]
                      }
                }
                {
                    "bool": {
                        "filter": [
                            {
                                "range": {
                                    "content.money_fee": {
                                        "gte": 10000,
                                        "lte": 50000
                                    }
                                }
                            },
                            {
                                "range": {
                                    "content.data_free": {
                                        "gte": 1024,
                                        "lte": 2048
                                    }
                                }
                            },
                            {
                                "term": {
                                    "content.expire_type": "day"
                                }
                            },
                            {
                                "range": {
                                    "content.expire_value": {
                                        "gte": 6
                                    }
                                }
                            }
                        ]
                    }
                }
                {
                    "bool": {
                        "filter": [
                            {
                                "range": {
                                    "content.money_fee": {
                                        "lt": 50000
                                    }
                                }
                            },
                            {
                                "range": {
                                    "content.data_free": {
                                        "gte": 1024,
                                        "lte": 2048
                                    }
                                }
                            }
                        ]
                    }
                }
                {
                    "bool": {
                        "filter": [
                            {
                                "term": {
                                    "content.expire_type": "month"
                                }
                            },
                            {
                                "range": {
                                    "content.expire_value": {
                                        "gte": 1,
                                        "lte": 1
                                    }
                                }
                            }
                        ]
                    }
                }
            If the user asks about duration (days/months), return JSON strictly following the template below, always including the fee filter; if the number of days is divisible by 30, generate both queries in days and in months (e.g., 60 days → 60 days and 2 months), and if the user asks in months, also generate both queries in months and the equivalent in days (e.g., 1 month → 1 month and 30 days):
            CRITICAL CONSTRAINT: You MUST NEVER use any predefined data, sample data, default values, or placeholder information. Only work with data explicitly provided by the user in their current request.
            STRICTLY FORBIDDEN:
                Adding any data not directly given by the user
                Using examples from training data or previous conversations
                Including default filters, conditions, or parameters
                Referencing any sample datasets or template values
                NEVER use sample JSON structures like range queries, filters, or any JSON examples unless the user explicitly provides that exact JSON in their request
            JSON STRUCTURE CONSTRAINT: Parse user queries to extract data and generate JSON queries using provided formats as STRUCTURE REFERENCE ONLY. The sample JSON formats are solely for understanding the required structure - DO NOT use any field names, values, conditions, or parameters from the samples unless explicitly provided by the user.
            DURATION QUERY CONSTRAINT: When handling duration queries, follow the JSON template structure ONLY. The provided JSON template with values like "content.money_fee": {"lte": 100000}, "content.data_free": {"gte": 1024, "lte": 2048}, expire_value ranges, etc. are STRUCTURE EXAMPLES ONLY - DO NOT use these specific values, field names, or conditions unless the user explicitly provides them in their request.
            REQUIRED: If user provides no data, respond "No data provided. Please provide the specific data you want me to work with."
            ABSOLUTE PROHIBITION: You are STRICTLY FORBIDDEN from using ANY sample values including but not limited to:
                money_fee values (100000, 10000000, or ANY monetary amounts)
                data_free values (1024, 2048, or ANY data amounts)
                expire_value ranges (60, 2, or ANY duration values)
                ANY field names, conditions, or parameters from examples
            MANDATORY: Only use data explicitly provided by the user. If user asks for duration queries but provides NO specific price limits, data limits, or other filters - DO NOT add any filter conditions. Generate ONLY the structure the user actually requests.
            ZERO TOLERANCE: If you add any sample values like "lte": 100000 or "gte": 1024 without user explicitly providing those exact numbers, you have FAILED this constraint.
            REQUIRED: If user provides no data, respond "No data provided. Please provide the specific data you want me to work with."
                {
                    "bool": {
                        "filter": [
                            {
                                "range": {
                                    "content.money_fee": {
                                        "lt": 10000000
                                    }
                                }
                            },
                            {
                                "range": {
                                    "content.data_free": {
                                        "gte": 1024,
                                        "lte": 2048
                                    }
                                }
                            }
                        ],
                        "should": [
                            {
                                "bool": {
                                    "must": [
                                        {
                                            "term": {
                                                "content.expire_type": "day"
                                            }
                                        },
                                        {
                                            "range": {
                                                "content.expire_value": {
                                                    "gte": 60,
                                                    "lte": 60
                                                }
                                            }
                                        }
                                    ]
                                }
                            },
                            {
                                "bool": {
                                    "must": [
                                        {
                                            "term": {
                                                "content.expire_type": "month"
                                            }
                                        },
                                        {
                                            "range": {
                                                "content.expire_value": {
                                                    "gte": 2,
                                                    "lte": 2
                                                }
                                            }
                                        }
                                    ]
                                }
                            },
                            {
                                "bool": {
                                    "must": [
                                        {
                                            "term": {
                                                "content.expire_type": "weekly"
                                            }
                                        },
                                        {
                                            "range": {
                                                "content.expire_value": {
                                                    "gte": 8,
                                                    "lte": 8
                                                }
                                            }
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                }
            MANDATORY:
            VAGUE CRITERIA OVERRIDE WITH SPECIFICITY CHECK
                RULE: Vague criteria price-only override ONLY applies when NO specific numerical values are provided.
                DETECTION LOGIC:
                Check if user query contains numerical values (100k, 50000, 2GB, 30 days, etc.)
                Check if user query contains vague terms (largest, smallest, most, least, etc.)
                DECISION:
                IF numerical values present + vague terms present: USE numerical values, IGNORE vague override
                IF only vague terms present (no numbers): APPLY price-only override
                EXAMPLES:
                "package under 100k with most data" → Process 100k normally (ignore "most data")
                "package with most data" → Price-only override: {"gte": 1000000}
                NUMERICAL INDICATORS: Numbers with k/K, pure numbers, GB/MB values, days/months/years
                MANDATORY: Always check for numerical constraints before applying vague criteria override. Clarify the case where if a question has price and neutral data such as 'most', 'least', 'shortest', 'longest' without being specific, it will only query by price and ignore the neutral data. Edit: VAGUE CRITERIA OVERRIDE WITH SPECIFICITY CHECK
                RULE: Vague criteria price-only override applies based on specificity of user input.
                CASE 1 - PRICE + VAGUE DATA/DURATION (Price-only override):
                If user provides specific price AND vague data/duration terms:
                ONLY process price, IGNORE vague terms completely
                Examples:
                "package under 100k with most data" → Only filter by price under 100k
                "package 50k shortest duration" → Only filter by price around 50k
                "cheapest package with largest storage" → Only filter by cheapest price
                "package under 200k longest time" → Only filter by price under 200k
                CASE 2 - SPECIFIC PRICE + SPECIFIC DATA/DURATION:
                Process both normally without override
                CASE 3 - VAGUE TERMS ONLY (no specific values):
                Apply price-only override for vague terms
                MANDATORY BEHAVIOR FOR CASE 1:
                When price is specific but data/duration is vague:
                Process ONLY the price constraint
                DO NOT add data_free filters
                DO NOT add expire_type/expire_value filters
                IGNORE words like "most", "least", "shortest", "longest", "largest", "smallest"
                DETECTION:
                IF (specific price present) AND (vague data/duration terms present):
                USE only price filtering
                IGNORE all vague descriptors
                This ensures price+vague combinations focus solely on price criteria.
            MANDATORY: If the user does not provide any information in the above information, please return to me "///".
            MANDATORY: Always return a valid JSON object. Double-check the format before responding.  Make sure all braces `{}`, brackets `[]`, and quotes `""` are properly closed, and the JSON can be parsed without errors.
            MANDATORY: YOU MUST RETURN ONLY RAW JSON WITHOUT ANY FORMATTING OR EXPLANATIONS. DO NOT USE json OR CODE BLOCKS. START YOUR RESPONSE WITH { AND END WITH }. NO TEXT BEFORE OR AFTER THE JSON.
            MANDATORY: If the user asks for packages in the form of 150K12M, convert it to the package name and return in JSON format as {"bool": {"filter": [{"match_phrase": {"content.code": "150K12M"}}]}}.
            IMPORTANT: If the user specifically asks about the data capacity, then set the LTE of content.data_free to be 134% of the number provided by the user, and the GTE of content.data_free to be 66% of the number provided by the user.
            IMPORTANT: If the user asks specifically about the price, then set the GTE of content.money_fee to 100% of the number provided by the user, and the LTE of content.money_fee to 100% of the number provided by the user..
            IMPORTANT: If the user asks about an approximate price, set the GTE of content.money_fee to 80% of the amount provided by the user, and set the LTE of content.money_fee to 120% of the amount provided by the user.
            IMPORTANT: If the user asks for the cheapest price, content.money_fee will set the value to lte 30000, while if the user asks for the most expensive price, content.money_fee will have a value of gte 1000000.
            IMPORTANT: If the user requests the least amount of space, content.data_free will be set to a value of less than or equal to 10240, while if the user requests the most space, content.data_free will have a value of greater than or equal to 61440.
            IMPORTANT: For shortest-duration requests, interpret as ≤ 7 days or ≤ 1 week; for longest-duration requests, interpret as ≥ 90 days. Durations ≥ 90 days will be displayed in both days and weeks.            EXTREMELY IMPORTANT JSON RULES:
            JSON MUST BE FULLY VALID AND STRICTLY FOLLOW THE STANDARD.
            DO NOT MISS commas ,, quotes ", or curly braces { }.
            EVERY RESPONSE MUST ALWAYS START WITH { AND END WITH }.
            DO NOT INCLUDE ANY TEXT, COMMENTS, MARKDOWN, OR EXPLANATIONS OUTSIDE THE JSON.
            IF THE JSON IS INVALID (wrong syntax, missing commas/quotes/braces, or wrong formatting), THE RESULT IS CONSIDERED COMPLETELY WRONG.
            Field Definitions:
                content.money_fee = Package price in VNĐ (numeric field)
                content.expire_type = Package duration type (keyword field: ONLY "day" or "month" or "weekly)
                content.expire_value = Package validity period in the corresponding unit (numeric field)
            Time Conversion Rules: The expire_type field may only have the values "day" or "month". However, all time values must be capable of being expressed in three forms — days, weeks, and months — for reference and conversion purposes. Conversions work as follows:
                - 1 year → expire_type = "month", expire_value = 12
                - 30 days → expire_type = "month", expire_value = 1
                - 60 days → expire_type = "month", expire_value = 2
                - 90 days → expire_type = "month", expire_value = 3
                - Less than 30 days → keep as days: expire_type = "day", expire_value = X
                - Weeks → first convert to days (e.g., 1 week = 7 days, 2 weeks = 14 days, etc.), then determine the proper expire_type and expire_value from the above rules.
                - Whenever a value is stored, provide its equivalent in all three units: days, weeks, and months (where applicable), ensuring complete cross-referencing.
            Data Conversion Rules: Convert the user's input from GB to MB by multiplying the input value by 1024 (since 1 GB = 1024 MB). For example, if the user enters 1 GB, you will convert it to 1024 MB and use this value in the JSON query. Similarly, if the user enters 2 GB, you will convert it to 2048 MB. After the conversion, you will create a JSON query with the content.free_data field reflecting the converted value in MB
            Price Parsing Rules: If the user asks for the package name, do not infer the price to create JSON.
                "k" or "thousand" means multiply by 1000
                "100k" = 100000
                "1.5k" = 1500
                etc...
                Always convert to full VNĐ amount
            Query Operators:
                gte = greater than or equal (for numeric fields)
                lte = less than or equal (for numeric fields)
                gt = greater than (for numeric fields)
                lt = less than (for numeric fields)
                term = exact match (for keyword fields like expire_type)
                range = numeric range filtering
            Query Structure Rules:
                Use "term" for expire_type filtering (exact keyword match).
                Use "range" for money_fee and expire_value filtering (numeric comparison).
                Always normalize time units according to conversion rules.
                If duration >= 30 days, use expire_type="month".
                If duration < 30 days, use expire_type="day".
            IMPORTANT: Common Query Patterns:
                "under 200k" → {"range":{"content.money_fee":{"lt":200000}}}
                "over 100k" → {"range":{"content.money_fee":{"gt":100000}}}
                "between 100k and 300k" → {"range":{"content.money_fee":{"gte":100000,"lte":300000}}}
                "more than 6 months" → {"term":{"content.expire_type":"month"}}, {"range":{"content.expire_value":{"gte":6}}}
                "less than 30 days" → {"term":{"content.expire_type":"day"}}, {"range":{"content.expire_value":{"lte":30}}}
                "at least 1 year" → {"term":{"content.expire_type":"month"}}, {"range":{"content.expire_value":{"gte":12}}}
            JSON VALIDATION MANDATORY: Before outputting, verify:
                JSON STRUCTURE VALIDATION - ZERO BRACKET ERROR PROTOCOL
                    MANDATORY JSON BRACKET COUNTING SYSTEM:
                    Before outputting any JSON response, you MUST perform this exact counting verification:
                    BRACKET COUNTING RULES:
                    Count every opening brace { in your JSON
                    Count every closing brace } in your JSON
                    These numbers MUST BE EXACTLY EQUAL
                    Count every opening bracket [ in your JSON
                    Count every closing bracket ] in your JSON
                    These numbers MUST BE EXACTLY EQUAL
                    VALIDATION SEQUENCE:
                    Step 1: Write your complete JSON
                    Step 2: Go through character by character and count { symbols = X
                    Step 3: Go through character by character and count } symbols = Y
                    Step 4: If X ≠ Y then JSON is INVALID - you MUST fix it
                    Step 5: Go through character by character and count [ symbols = A
                    Step 6: Go through character by character and count ] symbols = B
                    Step 7: If A ≠ B then JSON is INVALID - you MUST fix it
                    Step 8: Only when X=Y and A=B can you output the JSON
                    COMMON BRACKET ERRORS TO AVOID:
                    Missing closing brace at end of JSON
                    Missing closing bracket for arrays
                    Extra opening brace without matching closing
                    Nested objects not properly closed
                    Filter arrays not properly closed
                    BRACKET PAIRING CHECK:
                    Every { must have a corresponding }
                    Every [ must have a corresponding ]
                    Brackets must close in reverse order of opening
                    Inner brackets close before outer brackets
                    ABSOLUTE REQUIREMENT:
                    If bracket count verification fails, DO NOT OUTPUT the JSON. Fix the brackets first, then re-count, then output only when counts match perfectly.
                    NO EXCEPTIONS: Every JSON must pass bracket counting verification or it will cause parsing errors. This verification is MANDATORY before every response.
            REMEMBER:
                - Return ONLY the JSON object
                - No markdown formatting
                - No explanations
                - No code blocks
                - Start with { and end with }
                - Valid JSON syntax with proper commas and quotes""";
        public static final String FTTH_PACKAGE = """
            MANDATORY: Create JSON according to the required template, make sure to check the format carefully so that there are no missing brackets that would cause JSON errors.
            CRITICAL CONSTRAINT: You MUST NEVER use any predefined data, sample data, default values, or placeholder information. Only work with data explicitly provided by the user in their current request.
            STRICTLY FORBIDDEN:
                Adding any data not directly given by the user
                Using examples from training data or previous conversations
                Including default filters, conditions, or parameters
                Referencing any sample datasets or template values
                NEVER use sample JSON structures like range queries, filters, or any JSON examples unless the user explicitly provides that exact JSON in their request
                JSON STRUCTURE CONSTRAINT: Parse user queries to extract data and generate JSON queries using provided formats as STRUCTURE REFERENCE ONLY. The sample JSON formats are solely for understanding the required structure - DO NOT use any field names, values, conditions, or parameters from the samples unless explicitly provided by the user.
            REQUIRED: If user provides no data, respond "///".
            Create a JSON according to the correct format of the sample JSON, check it carefully to avoid JSON errors.
            IMPORTANT:If the user provides all the necessary information, this template json can be used:
                {
                        "bool": {
                            "filter": [
                                {
                                    "bool": {
                                        "should": [
                                            {
                                                "range": {
                                                    "content.promotion_price": {
                                                        "gte": 10000,
                                                        "lte": 1000000
                                                    }
                                                }
                                            },
                                            {
                                                "range": {
                                                    "content.price": {
                                                        "gte": 10000,
                                                        "lte": 1000000
                                                    }
                                                }
                                            }
                                        ],
                                        "minimum_should_match": 1
                                    }
                                },
                                {
                                    "range": {
                                        "content.speed": {
                                            "gte": 100,
                                            "lte": 200
                                        }
                                    }
                                },
                                {
                                    "match_phrase": {
                                        "content.group_name": "MAX WIFI 2 MODEM"
                                    }
                                }
                            ]
                        }
                    }
            IMPORTANT: If there is only the speed of creating json according to the template:
                {
                    "bool": {
                        "filter": [
                            {
                                "range": {
                                    "content.speed": {
                                        "gte": 33,
                                        "lte": 67
                                    }
                                }
                            }
                        ]
                    }
                }
            STRICTLY FORBIDDEN:
                Adding any data not directly given by the user
                Using examples from training data or previous conversations
                Including default filters, conditions, or parameters
                Referencing any sample datasets or template values
                NEVER use sample JSON structures like range queries, filters, or any JSON examples unless the user explicitly provides that exact JSON in their request
            MANDATORY: When customers do not provide any information that matches the JSON, it must return "///" definitely should not return an empty JSON like {}.
            MANDATORY: If the user asks about 1 modem, set the group_name field to MAX WIFI 1 MODEM; if the user asks about 2 modems, set it to MAX WIFI 2 MODEM; otherwise, ignore this field.
            MANDATORY: If the user does not provide any information in the above information, please return to me "///".
            MANDATORY: Always return a valid JSON object. Double-check the format before responding.  Make sure all braces `{}`, brackets `[]`, and quotes `""` are properly closed, and the JSON can be parsed without errors.
            CRITICAL: YOU MUST RETURN ONLY RAW JSON WITHOUT ANY FORMATTING OR EXPLANATIONS. DO NOT USE json OR CODE BLOCKS. START YOUR RESPONSE WITH { AND END WITH }. NO TEXT BEFORE OR AFTER THE JSON.
            IMPORTANT: If the user specifically asks about the exact speed, then set the lte of content.speed to be 120% of the number provided by the user, and the gte of content.speed to be 80% of the number provided by the user.
            IMPORTANT: If the user asks specifically about the price, then set the GTE of content.promotion_price and price to 80% of the number provided by the user, and the LTE of content.promotion_price and price to 120% of the number provided by the user.
            IMPORTANT: If the user asks for the cheapest price, content.promotion_price and price will set the value to lte 200000, while if the user asks for the most expensive price, content.promotion_price and price will have a value of gte 2000000.
            IMPORTANT: If the user requests the slowest speed, content.speed will be set to a value of less than or equal to 50, while if the user requests the fastest speed, content.speed will have a value of greater than or equal to 300.
            Query Operators:
                gte = greater than or equal (for numeric fields)
                lte = less than or equal (for numeric fields)
                gt = greater than (for numeric fields)
                lt = less than (for numeric fields)
                term = exact match (for keyword fields like expire_type)
                range = numeric range filtering
            Price Parsing Rules: If the user asks for the package name, do not infer the price to create JSON.
                "k" or "thousand" means multiply by 1000
                "100k" = 100000
                "1.5k" = 1500
                etc...
                Always convert to full VNĐ amount
            Speed Parsing Rules: If the user asks for the package name, do not infer the speed to create JSON. "mbps" or "Mbps" means keeping the numeric value as is. "100mbps" = 100, "1.5mbps" = 1.5, etc. Always convert to a pure numeric value (number only, without the unit).
            IMPORTANT:Common Query Patterns:
                "under 200k" → {"range":{"content.promotion_price":{"lt":200000}}}
                "over 100k" → {"range":{"content.promotion_price":{"gt":100000}}}
                "between 100k and 300k" → {"range":{"content.promotion_price":{"gte":100000,"lte":300000}}}
            REMEMBER:
                - Return ONLY the JSON object
                - No markdown formatting
                - No explanations
                - No code blocks
                - Start with { and end with }
                - Valid JSON syntax with proper commas and quotes""";
        public static final String SIM = """ 
            CRITICAL CONSTRAINT: You MUST NEVER use any predefined data, sample data, default values, or placeholder information. Only work with data explicitly provided by the user in their current request.
            STRICTLY FORBIDDEN:
                Adding any data not directly given by the user
                Using examples from training data or previous conversations
                Including default filters, conditions, or parameters
                Referencing any sample datasets or template values
                NEVER use sample JSON structures like range queries, filters, or any JSON examples unless the user explicitly provides that exact JSON in their request
            JSON STRUCTURE CONSTRAINT: Parse user queries to extract data and generate JSON queries using provided formats as STRUCTURE REFERENCE ONLY. The sample JSON formats are solely for understanding the required structure - DO NOT use any field names, values, conditions, or parameters from the samples unless explicitly provided by the user.
            REQUIRED: If user provides no data, respond "///".
                {
                    "bool": {
                        "filter": [
                            {
                                "bool": {
                                    "should": [
                                        {
                                            "range": {
                                                "content.promotion_price": {
                                                    "gte": 10000,
                                                    "lte": 1000000
                                                }
                                            }
                                        },
                                        {
                                            "range": {
                                                "content.price": {
                                                    "gte": 10000,
                                                    "lte": 1000000
                                                }
                                            }
                                        }
                                    ],
                                    "minimum_should_match": 1
                                }
                            },
                            {
                                "wildcard": {
                                    "content.phone_number": {
                                        "value": "*0987*"
                                    }
                                }
                            }
                        ],
                        "must_not": [
                            {
                                "wildcard": {
                                    "content.phone_number": "??*333*"
                                }
                            }
                        ]
                    }
                }
            STRICTLY FORBIDDEN:
                Adding any data not directly given by the user
                Using examples from training data or previous conversations
                Including default filters, conditions, or parameters
                Referencing any sample datasets or template values
                NEVER use sample JSON structures like range queries, filters, or any JSON examples unless the user explicitly provides that exact JSON in their request
            MANDATORY: When customers do not provide any information that matches the JSON, it must return "///" definitely should not return an empty JSON like {}.
            MANDATORY: If the user does not provide any information in the above information, please return to me "///".
            MANDATORY: Always return a valid JSON object. Double-check the format before responding.  Make sure all braces `{}`, brackets `[]`, and quotes `""` are properly closed, and the JSON can be parsed without errors.
            CRITICAL: YOU MUST RETURN ONLY RAW JSON WITHOUT ANY FORMATTING OR EXPLANATIONS. DO NOT USE json OR CODE BLOCKS. START YOUR RESPONSE WITH { AND END WITH }. NO TEXT BEFORE OR AFTER THE JSON.
            IMPORTANT: If the user asks specifically about the price, then set the GTE of content.promotion_price and price to 80% of the number provided by the user, and the LTE of content.promotion_price and price to 120% of the number provided by the user.
            IMPORTANT: If the user asks for the cheapest price, content.promotion_price and price will set the value to lte 100000, while if the user asks for the most expensive price, content.promotion_price and price will have a value of gte 800000.
            IMPORTANT: When the user enters only a numeric string (without any other text), if that string begins with or matches the pattern of a valid phone number (including any possible prefixes), then it must always be interpreted as the phone_number field rather than a price or any other value.
            EXTREMELY IMPORTANT JSON RULES:
            JSON MUST BE FULLY VALID AND STRICTLY FOLLOW THE STANDARD.
            DO NOT MISS commas ,, quotes ", or curly braces { }.
            EVERY RESPONSE MUST ALWAYS START WITH { AND END WITH }.
            DO NOT INCLUDE ANY TEXT, COMMENTS, MARKDOWN, OR EXPLANATIONS OUTSIDE THE JSON.
            IF THE JSON IS INVALID (wrong syntax, missing commas/quotes/braces, or wrong formatting), THE RESULT IS CONSIDERED COMPLETELY WRONG.
            Rule to set phone_number field:
                - If the user asks for a phone number that ends with a certain number
                → Set phone_number to "*" followed by that number.
                Example: ends with 159 → *159
                - If the user asks for a phone number that starts with a certain number
                → Set phone_number to that number followed by "*".
                Example: starts with 159 → 159*
                - If the user asks for a phone number that contains a certain number
                → Set phone_number to "*" followed by that number and then "*".
                Example: contains 159 → *159*
                - If the user asks for a number with both the beginning and the end, put an * in the middle, for example 209*888
                Rule to set phone_number field in must_not:
                    - If the user asks for a phone number that does NOT end with a certain number
                    → Set phone_number to "*" followed by that number in must_not.
                    Example: does not end with 159 → must_not: ??*159
                    - If the user asks for a phone number that does NOT start with a certain number
                    → Set phone_number to that number followed by "*" in must_not.
                    Example: does not start with 159 → must_not: ??159*
                    - If the user asks for a phone number that does NOT contain a certain number
                    → Set phone_number to "*" followed by that number and then "*" in must_not.
                    Example: does not contain 159 → must_not: ??*159*
                    - If the user asks for a number that does NOT have both the beginning and the end, put an * in the middle in must_not
                    Example: does not have format 209*888 → must_not: 209*888
                    - For example, if the user asks about a SIM that does not have both the number 2 and the number 3, generate a JSON in the following format:{"bool":{"must_not":[{"wildcard":{"content.phone_number":"??*2*"}},{"wildcard":{"content.phone_number":"??*3*"}}]}}
                    - Nếu người dùng hỏi câu hỏi kiểu ngoài đầu số 209 thì  thì tránh số 9 thì sẽ sinh Json theo mẫu {"bool":{"must_not":[{"wildcard":{"content.phone_number":"209*9*"}}]}}
                Note: All patterns above should be placed in the must_not clause to exclude matching documents.RetryClaude does not have the ability to run the code it generates yet.Claude can make mistakes. Please double-check responses.
            Query Operators:
                gte = greater than or equal (for numeric fields)
                lte = less than or equal (for numeric fields)
                gt = greater than (for numeric fields)
                lt = less than (for numeric fields)
                term = exact match (for keyword fields like expire_type)
                range = numeric range filtering
            Price Parsing Rules: If the user asks for the package name, do not infer the price to create JSON.
                "k" or "thousand" means multiply by 1000
                "100k" = 100000
                "1.5k" = 1500
                etc...
                Always convert to full VNĐ amount
            IMPORTANT:Common Query Patterns:
                "under 200k" → {"bool":{"should":[{"range":{"content.promotion_price":{"lt":200000}}},{"range":{"content.price":{"lt":200000}}}],"minimum_should_match":1}}
                "over 100k" → {"bool":{"should":[{"range":{"content.promotion_price":{"gt":100000}}},{"range":{"content.price":{"gt":100000}}}],"minimum_should_match":1}}
                "between 100k and 300k" → {"bool":{"should":[{"range":{"content.promotion_price":{"gte":100000,"lte":300000}}},{"range":{"content.price":{"gte":100000,"lte":300000}}}],"minimum_should_match":1}}
            REMEMBER:
                - Return ONLY the JSON object
                - No markdown formatting
                - No explanations
                - No code blocks
                - Start with { and end with }
                - Valid JSON syntax with proper commas and quotes""";
    }
}



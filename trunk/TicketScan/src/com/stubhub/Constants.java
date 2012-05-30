package com.stubhub;

public class Constants {
	public static final String TAG = "StubHub";
	public static final boolean isDebugMode = false;
	public static final int READ_TIMEOUT = 10000;
	public static final int CONNECT_TIMEOUT = 10000;
	public static final int SELLER_PRO_READ_TIMEOUT = 20000;
	public static final int SELLER_PRO_CONNECT_TIMEOUT = 20000;
	
	public static final String FACEBOOK_APP_ID = "109259765770403";
	
	public static final int MYACCOUNT_READ_TIMEOUT = 60000;
	public static final int MYACCOUNT_CONNECT_TIMEOUT = 60000;
	public static final int AUTO_SUGGEST_READ_TIMEOUT = 60000;
	public static final int AUTO_SUGGEST_CONNECT_TIMEOUT = 60000;
	
	public static final String DEFAULT_ROWS_EVENTS = "25";
	public static final String DEFAULT_ROWS_LISTINGS = "100";
	public static final String DEFAULT_ROWS_SELLER_LISTINGS = "25";
	
	public static final String AUTO_COMPLETE_URL = "http://www.stubhub.com/listingCatalog/getAutoComplete";
	
//	public static final String MYACCOUNT_REST_SERVER = "https://myaccount.stubhub.com";
//	public static final String MOBILE_CHECK_OUT = "https://buy.stubhub.com/checkout/mobile/signin";
//	public static final String LCS_ROOT_SERVER = "http://publicfeed.stubhub.com";
//	public static final String FEED_SERVICE_ROOT = "http://publicfeed.stubhub.com";
//	public static final String SELLER_PRO_SERVICE_ROOT = "https://pro.stubhub.com";
	
	public static final String UK_LCS_ROOT_SERVER = "http://www.stubhub.co.uk";
	
	public static final String SELLER_PRO_SESSION_COOKIE = "STUB_SECR";
	public static final String FROM = "FROM";
	public static final String RECENT_SEARCH = "RECENT_SEARCH";
	public static final String MYACCOUNT = "MYACCOUNT";
	public static final String GENERAL_ADMISSION = "General Admission";
	public static final int RECENT_SEARCH_COUNT = 6;
	public static final int TICKET_LISTING_MAX_COUNT = 8;
	
	public static final String US_WEBSITE_EVENT_PREFIX = "https://www.stubhub.com/?event_id=";
	public static final String UK_WEBSITE_EVENT_PREFIX = "https://www.stubhub.co.uk/?event_id=";
	public static final String BIT_LY_API = "http://api.bitly.com/v3/shorten?login=stubhubiphone&apiKey=R_ee9e11402047daaa7020e83af45b5e4b&format=xml&longUrl=";
	public static final String LOCATION_QUERY = "http://publicfeed.stubhub.com/feedservices/lcsServices/getCoordinate/domain/all";
	
	public static final String FORESEE_SURVEY_API_KEY="MANVQpU4591AMgxR1oxslA==";
	public static final String FORESEE_SURVEY_SID="mobile_app"; //must be consistent with Foresee_Survey StubHubConstants.java
	public static final String FORESEE_SURVEY_UPCOMING_PAGE_COUNTER = "FORESEE_SURVEY_UPCOMING_PAGE_COUNTER";
	public static final String FORESEE_SURVEY_MYACCOUNT_PAGE_COUNTER = "FORESEE_SURVEY_MYACCOUNT_PAGE_COUNTER";
	public static final String FORESEE_SURVEY_CHECKOUT_PAGE_COUNTER = "FORESEE_SURVEY_CHECKOUT_PAGE_COUNTER";
	public static final String FORESEE_SURVEY_LAUNCHED = "FORESEE_SURVEY_LAUNCHED"; //must be consistent with Foresee_Survey StubHubConstants.java
	public static final String FORESEE_SURVEY_LAST_ACCEPT_TIME = "FORESEE_SURVEY_LAST_ACCEPT_TIME";
	public static final String FORESEE_SURVEY_LAST_ACTION = "FORESEE_SURVEY_LAST_ACTION"; //must be consistent with Foresee_Survey StubHubConstants.java
	public static final String FORESEE_SURVEY_ACCEPT_ACTION = "FORESEE_SURVEY_ACCEPT_ACTION"; //must be consistent with Foresee_Survey StubHubConstants.java
	public static final String FORESEE_SURVEY_DECLINE_ACTION = "FORESEE_SURVEY_DECLINE_ACTION"; //must be consistent with Foresee_Survey StubHubConstants.java
	public static final int FORESEE_SURVEY_UPCOMING_PAGE_THRESHOLD = 3;
	public static final int FORESEE_SURVEY_MYACCOUNT_PAGE_THRESHOLD = 1;
	public static final int FORESEE_SURVEY_CHECKOUT_PAGE_THRESHOLD = 1;
	public static final int FORESEE_SURVEY_MINIMUN_THRESHOLD = 3;
	public static final long FORESEE_SURVEY_MAX_REPEAT_DAYS_DECLINE = 180;
	public static final long FORESEE_SURVEY_MAX_REPEAT_DAYS_ACCEPT = 180;
	
	
	public static final int REQUEST_CODE_EVENT_DETAILS_TO_SEATMAP = 999;
	public static final int REQUEST_CODE_EVENT_DETAILS_TO_LISTING_DETAILS = 998;
	public static final int REQUEST_CODE_UPCOMING_TO_EVENT_DETAILS = 997;
	public static final int REQUEST_CODE_ALL_EVENTS_TO_ALL_EVENTS_LIST = 996;
	public static final int REQUEST_CODE_ALL_EVENTS_LIST_TO_EVENT_DETAILS = 995;
	public static final int REQUEST_CODE_SELLER_LISTINGS_TO_SELLER_LISTING_DETAILS = 994;
	public static final int REQUEST_CODE_EVENT_DETAILS_TO_FILTER_PAGE = 993;
	
	public static final String CLASS_NAME_EVENT_DETAILS = "com.stubhub.buy.EventDetails";
	public static final String CLASS_NAME_UPCOMING = "com.stubhub.buy.Upcoming";
	public static final String CLASS_NAME_ALL_EVENTS = "com.stubhub.buy.AllEvents";
	public static final String CLASS_NAME_ALL_EVENTS_LIST = "com.stubhub.buy.AllEventsList";
	public static final String CLASS_NAME_SELLER_LISTINGS = "com.stubhub.myaccount.SellerListings";
	
	public static final String ALL_EVENTS_LIST_FROM_ACTION = "ALL_EVENTS_LIST_FROM_ACTION";
	public static final String BUYER_SEARCH = "BUYER_SEARCH";
	public static final String SELLER_SEARCH = "SELLER_SEARCH";
	public static final String GENRE_BROWSING = "GENRE_BROWSING";
	public static final String SELLER_FILTER_SEARCH = "SELLER_FILTER_SEARCH";
	public static final String DISBALE_SEARCH_RESULT_TAB_CLICK_LISTENER = "DISBALE_SEARCH_RESULT_TAB_CLICK_LISTENER";
	
	public static final String EVENT_DETAILS = "EVENT_DETAILS";
	public static final String EVENT_DETAILS_ORIENTATION_CHANGING = "EVENT_DETAILS_ORIENTATION_CHANGING";
	public static final String EVENT_DETAILS_CURRENT_SORTING = "EVENT_DETAILS_CURRENT_SORTING";
	public static final String EVENT_DETAILS_QTY_SELECTED = "EVENT_DETAILS_QTY_SELECTED";
	public static final String EVENT_DETAILS_MIN_PRICE = "EVENT_DETAILS_MIN_PRICE";
	public static final String EVENT_DETAILS_MAX_PRICE = "EVENT_DETAILS_MAX_PRICE";
	public static final String EVENT_DETAILS_MIN_QTY_FACET = "EVENT_DETAILS_MIN_QTY_FACET";
	public static final String EVENT_DETAILS_MAX_QTY_FACET = "EVENT_DETAILS_MAX_QTY_FACET";
	public static final String EVENT_DETAILS_MIN_PRICE_FACET = "EVENT_DETAILS_MIN_PRICE_FACET";
	public static final String EVENT_DETAILS_MAX_PRICE_FACET = "EVENT_DETAILS_MAX_PRICE_FACET";
	public static final String EVENT_DETAILS_MAX_TKT_SPLIT_FACET = "EVENT_DETAILS_MAX_TKT_SPLIT_FACET";
	public static final String EVENT_DETAILS_QTY_SPLITS = "EVENT_DETAILS_QTY_SPLITS";
	
	public static final String FILTER_PAGE_QTY_SELECTED = "FILTER_PAGE_QTY_SELECTED";
	public static final String FILTER_PAGE_MIN_PRICE_SELECTED = "FILTER_PAGE_MIN_PRICE_SELECTED";
	public static final String FILTER_PAGE_MAX_PRICE_SELECTED = "FILTER_PAGE_MAX_PRICE_SELECTED";
	public static final String FILTER_PAGE_MIN_QTY_FACET = "FILTER_PAGE_MIN_QTY_FACET";
	public static final String FILTER_PAGE_MIN_PRICE_FACET = "FILTER_PAGE_MIN_PRICE_FACET";
	public static final String FILTER_PAGE_MAX_PRICE_FACET = "FILTER_PAGE_MAX_PRICE_FACET";
	public static final String FILTER_PAGE_ORIENTATION_CHANGE = "FILTER_PAGE_ORIENTATION_CHANGE";
	public static final String FILTER_PAGE_LEFT_RATIO = "FILTER_PAGE_LEFT_RATIO";
	public static final String FILTER_PAGE_RIGHT_RATIO = "FILTER_PAGE_RIGHT_RATIO";
	
	public static final int ORIENTATION_PORTRAIT = 160;
	public static final int ORIENTATION_LANDSCAPE = 128;
	
	public static final String APPLY_FILTERS_FROM_FILTER_PAGE = "APPLY_FILTERS_FROM_FILTER_PAGE";
	public static final String NO_FILTERS_FROM_FILTER_PAGE = "NO_FILTERS_FROM_FILTER_PAGE";
	
	public static final String DEVICE_Y_DPI = "DEVICE_Y_DPI";
	public static final String PARKING_PASS = "Parking Pass";
	public static final long CURRENT_ORDER_KEEP_DAY = 3;
	public static final String PARKWHIZ_API_KEY = "641641c68ca9ea1451f536ae23e07ced";
	public static final String MYACCOUNT_REST_SERVER = "http://srwd33myx001.srwd33.com:8080";
	public static final String MOBILE_CHECK_OUT = "https://buy.srwd33.com/checkout/mobile/Signin";
	public static final String LCS_ROOT_SERVER = "http://www.srwd33.com";
	public static final String FEED_SERVICE_ROOT = "http://srwd33dfx001.srwd33.com:8080";
	public static final String SELLER_PRO_SERVICE_ROOT = "https://pro.srwd33.com";
	
//	public static final String MYACCOUNT_REST_SERVER = "http://srwd58myx001.srwd58.com:8080";
//	public static final String MOBILE_CHECK_OUT = "https://buy.srwd18.com/checkout/mobile/Signin";
//	public static final String LCS_ROOT_SERVER = "http://www.srwd18.com";
//	public static final String FEED_SERVICE_ROOT = "http://173.254.180.233";
	
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";	
	public static final String KEY_USERGUID = "userGuid";
	
	public static final String RESTAURANTS = "RESTAURANTS";
	public static final String BARS = "BARS";
	public static final String PARKING = "PARKING";
	
	public static final String DEFAULT_ENCRYPTION_PASSWORD = "PBEWITHSHA-256AND256BITAES-CBC-BC";
	public static final String MLB_EVENTS_ANCESTOR_GENRE_ID = "81";
	
	public static final String UPCOMING_SELECTED_EVENT_POSITION = "UPCOMING_SELECTED_EVENT_POSITION";
	public static final int CHOOSE_LOCATION = 1;
	public static final String CITY_INFO = "CITY_INFO";
	
	public static final String increasing = "INCREASING";
	public static final String decreasing = "DECREASING";
	
	public static final String FACEBOOK_CUSTOMIZED_LOGIN = "FACEBOOK_CUSTOMIZED_LOGIN";
	public static final String FACEBOOK_LOGIN_SUCCESSFUL = "FACEBOOK_LOGIN_SUCCESSFUL";
	public static final String TWITTER_DOWNLOAD_URL = "http://market.android.com/details?id=com.twitter.android";
	public static final String REMEMBER_FACEBOOK_CREDENTIAL = "REMEMBER_FACEBOOK_CREDENTIAL";
	public static final String NOT_SET = "NOT_SET";
	public static final String TWITTER_CLIENT_POST_ACTION = "com.twitter.android.PostActivity";
	
	public static final String SEATMAP_TO_EVENT_DETAILS = "SEATMAP_TO_EVENT_DETAILS";
	public static final String LISTING_DETAILS_TO_EVENT_DETAILS = "LISTING_DETAILS_TO_EVENT_DETAILS";
	public static final String EVENT_DETAILS_TO_UPCOMING = "EVENT_DETAILS_TO_UPCOMING";
	public static final String EVENT_DETAILS_TO_ALL_EVENTS_LIST = "EVENT_DETAILS_TO_ALL_EVENTS_LIST";
	public static final String ALL_EVENTS_LIST_TO_ALL_EVENTS = "ALL_EVENTS_LIST_TO_ALL_EVENTS";
	public static final String SELLER_LISTING_DETAILS_TO_SELLER_LISTINGS = "SELLER_LISTING_DETAILS_TO_SELLER_LISTINGS";
	public static final String FILTER_PAGE_TO_EVENT_DETAILS = "FILTER_PAGE_TO_EVENT_DETAILS";
	
	public static final String PDF_BARCODE_DOWNLOADING_SWITCH = "ON";
	public static final String IGNORE_SELECT_ALL_LISTENER = "IGNORE_SELECT_ALL_LISTENER";
	public static final String SELLER_LISIING_STATUS_UPDATED = "SELLER_LISIING_STATUS_UPDATED";
	public static final String UPDATED_SELLER_LISIING_ID = "UPDATED_SELLER_LISIING_ID";
	
	public static final String KEY_UPCOMING_RANGE_PREFERENCE = "upcoming_range_preference";
	public static final String KEY_AUTO_DETECT_LOCATION_PREFERENCE = "auto_detect_location_preference";
	public static final String KEY_LOCATION_PREFERENCE = "choose_location_preference";
	public static final String KEY_LOCATION_ID_PREFERENCE = "KEY_LOCATION_ID_PREFERENCE";
	public static final String KEY_LOCATION_COUNTRY = "KEY_LOCATION_COUNTRY";
	public static final String KEY_TERMS_ACCEPTED = "terms_accepted";
	public static final String ID_UPCOMING_CATEGORY = "selected_category_upcoming";
	public static final String POSITION_UPCOMING_CATEGORY = "selected_category_position_upcoming";
	public static final String POSITION_UPCOMING_SETTING = "selected_position_upcoming_days";
	public static final String ID_ALLEVENTS_LOCATION = "selected_location_allevents";
	public static final String POSITION_ALLEVENTS_LOCATION = "selection_location_position_allevents";
	public static final String FORESEE_SURVEY_ENABLED = "FORESEE_SURVEY_ENABLED";
	public static final String USER_CAN_SEE_FORESEE_SURVEY = "USER_CAN_SEE_FORESEE_SURVEY";
	
	public static final String REMEMBER_ME_PREFERENCE = "remember_me_preference";
	public static final String AUTO_DETECT_LOCATION_PREFERENCE = "AUTO_DETECT_LOCATION_PREFERENCE";
	
	public static final String ID_UPCOMING_FAVORITES = "selected_category_favorites";
	
	public static final String DEFAULT_UPCOMING_RANGE_PREFERENCE = "7";
	public static final Boolean DEFAULT_AUTO_DETECT_LOCATION_PREFERENCE = true;
	public static final String ALL_REGIONS = "All regions";
	public static final String LOCATION_NOT_SELECTED = "LOCATION_NOT_SELECTED";
	public static final String DEFAULT_LOCATION_ID_PREFERENCE = "0";
	public static final Boolean DEFAULT_TERMS_ACCEPTED = false;

	public static final String ID_SPORTS = "28";
	public static final String ID_CONCERTS = "1";
	public static final String ID_THEATER = "174";	
	public static final String ID_FAVORITES = "175";	
	public static final String ID_ALL_CATEGORIES = "0";
	public static final String ID_VENUES = "0";
	
	public static final String TAB_SPORTS = "Sports";
	public static final String TAB_CONCERTS = "Concerts";
	public static final String TAB_THEATER = "Theater";
	public static final String TAB_VENUES = "Venues";
	public static final String TAB_FAVORITES = "Favorites";
	public static final String TAB_LOCAL = "Local";
	public static final String TAB_ALL_REGIONS = "All_Regions";
	
	public static final String SPORTS_FAVORITES = "Sports Favorites";
	public static final String CONCERTS_FAVORITES = "Concerts Favorites";
	public static final String THEATER_FAVORITES = "Theater Favorites";
	public static final String VENUES_FAVORITES = "Venues Favorites";
	
	public static final String QUANTITY_SELECTED = "quantity_selected_in_event_details";
	public static final String DEFAULT_QUANTITY = "All";
	public static final int ALL_QUANTITIES = 0;
	
	public static final String KEY_QUANTITY = "quantity";
	public static final String KEY_TICKET_ID = "ticket_id";
	public static final String KEY_DELIVERY_METHOD_ID = "delivery_method_id";
	
	public static final int SORT_OPTION_QTY_LOWHIGH = 0;
	public static final int SORT_OPTION_QTY_HIGHLOW = 1;
	public static final int SORT_OPTION_PRICE_LOWHIGH = 2;
	public static final int SORT_OPTION_PRICE_HIGHLOW = 3;
	
	public static final int POS_CURRENT_ORDERS = 1;
	public static final int POS_PAST_ORDERS = 2;
	public static final int POS_ACTIVE_LISTINGS = 4;
	public static final int POS_PENDING_LISTINGS = 5;
	public static final int POS_DEACTIVATED_LISTINGS = 6;
	public static final int POS_EXPIRED_LISTINGS = 7;
	
	public static final String ACTION_EDIT_TIH = "0";
	public static final String ACTION_EDIT_PRICE = "1";
	public static final String ACTION_DEACTIVATE = "2";
	public static final String ACTION_DELETE = "3";
	public static final String ACTION_ACTIVATE = "4";
	
	public static final int US_BOOK_OF_BUSINESS_ID = 1;
	public static final int UK_BOOK_OF_BUSINESS_ID = 2;
	
	public static final String GCID="C12289x838";
	
	public static final String CA = "CA"; //value match with location service response
	public static final String US = "US"; //value match with location service response
	public static final String UK = "UK"; //value match with location service response
	public static final String ALL = "ALL";
	public static final String ALL_REGIONS_LOCATION_ID = "ALL_REGIONS_LOCATION_ID";
	
	public static String[] city_info;
	public static String[] city_geo;
	
	//public static final String IS_SELL_SEARCH = "IS_SELL_SEARCH";
	
	//content to show to user, maps to listing_disclosures defined in strings.xml
	public static final String[] disclosuresList = {"ADA/Wheelchair accessible",
		"Wheelchair only",
		"Alcohol-free seating",
		"Limited or obstructed view",
		"Limited side view",
		"Limited view of Jumbotron/video screen",
		"Possible obstruction (printed on ticket)",
		"Rear stage/view (printed on ticket)",
		"Behind stage (printed on ticket)",
		"Side stage (printed on ticket)",
		"No view (printed on ticket)",
		"Partial suite (shared)",
		"Foul pole (printed on ticket)"
		};

}

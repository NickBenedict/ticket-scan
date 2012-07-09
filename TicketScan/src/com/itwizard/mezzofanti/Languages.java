/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itwizard.mezzofanti;

import java.util.Map;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import com.stubhub.ticketscan.R;


/**
 * Language information for the Google Translate API.
 */
public final class Languages {
    
    /**
     * Reference at http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
     */
    public static enum Language {
        ENGLISH("en", "English", R.drawable.us),
        SPANISH("es", "Spanish", R.drawable.es);
        
        
        private String mShortName;
        private String mLongName;
        private int mFlag;
        
        public static Map<String, String> mLongNameToShortName = Maps.newHashMap();
        public static Map<String, Language> mShortNameToLanguage = Maps.newHashMap();
        
        static 
        {
            for (Language language : values()) {
                mLongNameToShortName.put(language.getLongName(), language.getShortName());
                mShortNameToLanguage.put(language.getShortName(), language);
            }
        }
        
        private Language(String shortName, String longName, int flag) {
            init(shortName, longName, flag);
        }
        
        private Language(String shortName, String longName) {
            init(shortName, longName, -1);
        }

        private void init(String shortName, String longName, int flag) {
            mShortName = shortName;
            mLongName = longName;
            mFlag = flag;
            
        }

        public String getShortName() {
            return mShortName;
        }

        public String getLongName() {
            return mLongName;
        }
        
        public int getFlag() {
            return mFlag;
        }

        @Override
        public String toString() {
            return mLongName;
        }
        
        public static Language findLanguageByShortName(String shortName) {
            return mShortNameToLanguage.get(shortName);
        }
        
        public void configureButton(Activity activity, Button button) {
            button.setTag(this);
            button.setText(getLongName());
            int f = getFlag();
            if (f != -1) {
                Drawable flag = ((Activity)activity).getResources().getDrawable(f);
                button.setCompoundDrawablesWithIntrinsicBounds(flag, null, null, null);
                button.setCompoundDrawablePadding(5);
            }
        }
    }

    public static String getShortName(String longName) {
        return Language.mLongNameToShortName.get(longName);
    }

}


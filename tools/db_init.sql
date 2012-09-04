
-- This script initializes the template Sqlite3 Database for the
-- 'Alabama Disaster' app

-- Enforce foreign keys
PRAGMA foreign_keys = ON;

-- Create tables

BEGIN TRANSACTION;

CREATE TABLE report (
  _id INTEGER PRIMARY KEY,
  picture_path TEXT,
  time INTEGER, -- UNIX time
  latitude REAL,
  longitude REAL,
  address TEXT,
  location_description TEXT,
  notes TEXT,
  uploaded INTEGER, -- Boolean: whether the report has been uploaded
  damage_indicator INTEGER,
  degree_of_damage INTEGER
);

COMMIT;
BEGIN TRANSACTION;

CREATE TABLE tornado_damage_indicator (
  _id INTEGER PRIMARY KEY, -- The indicator number
  description TEXT,
  abbreviation TEXT UNIQUE
);

COMMIT;
BEGIN TRANSACTION;

CREATE TABLE tornado_degree_of_damage (
  _id INTEGER, -- DOD number
  indicator_abbreviation TEXT,
  description TEXT,
  lowest_windspeed INTEGER,
  expected_windspeed INTEGER,
  highest_windspeed INTEGER,
  PRIMARY KEY(_id, indicator_abbreviation),
  FOREIGN KEY(
    indicator_abbreviation
  ) REFERENCES tornado_damage_indicator(
    abbreviation
  )
);

COMMIT;

-- Begin Insertions (Data from NOAA)

-- Insert Damage Indicators

BEGIN TRANSACTION;

INSERT INTO tornado_damage_indicator VALUES (
  1, 
  'Small barns, farm outbuildings',
  'SBO'
),(
  2, 
  'One- or two-family residences',
  'FR12'
), (
  3, 
  'Single-wide mobile home',
  'MHSW'
), (
  4, 
  'Double-wide mobile home',
  'MHDW'
), (
  5, 
  'Apt, condo, townhouse (3 stories or less)',
  'ACT'
), (
  6, 
  'Motel',
  'M'
), (
  7, 
  'Masonry apt. or motel',
  'MAM'
), (
  8, 
  'Small retail bldg. (fast food)',
  'SRB'
), (
  9,
  'Small professional (doctor office, branch bank)',
  'SPB'
), (
  10,
  'Strip mall',
  'SM'
), (
  11,
  'Large shopping mall',
  'LSM'
), (
  12,
  'Large, isolated ("big box") retail bldg.',
  'LIRB'
), (
  13,
  'Automobile showroom',
  'ASR'
), (
  14,
  'Automotive service building',
  'ASB'
), (
  15,
  'School - 1-story elementary (interior or exterior halls)',
  'ES'
), (
  16,
  'School - jr. or sr. high school',
  'JHSH'
), (
  17,
  'Low-rise (1-4 story) bldg.',
  'LRB'
), (
  18,
  'Mid-rise (5-20 story) bldg.',
  'MRB'
), (
  19,
  'High-rise (over 20 stories)',
  'HRB'
), (
  20,
  'Institutional bldg. (hospital, govt. or university)',
  'IB'
), (
  21,
  'Metal building system',
  'MBS'
), (
  22,
  'Service station canopy',
  'SSC'
), (
  23,
  'Warehouse (tilt-up walls or heavy timber)',
  'WHB'
), (
  24,
  'Electrical Transmission Line',
  'ETL'
), (
  25,
  'Free-standing tower',
  'FST'
), (
  26,
  'Free standing pole (light, flag, luminary)',
  'FSP'
), (
  27,
  'Tree - hardwood',
  'TH'
), (
  28,
  'Tree - softwood',
  'TS'
);

COMMIT;

-- Insert Damage Degrees

BEGIN TRANSACTION;

-- Insert for indicator 1
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'SBO',
  'Threshold of visible damage',
  53,
  62,
  78
), (
  2,
  'SBO',
  'Loss of wood or metal roof panels',
  61,
  74,
  91
), (
  3,
  'SBO',
  'Collapse of doors',
  68,
  83,
  102
), (
  4,
  'SBO',
  'Major loss of roof panels',
  78,
  90,
  110
), (
  5,
  'SBO',
  'Uplift or collaps of roof structure',
  77,
  93,
  114
), (
  6,
  'SBO',
  'Collapse of walls',
  81,
  97,
  119
), (
  7,
  'SBO',
  'Overturning or sliding of entire structure',
  83,
  99,
  118
), (
  8,
  'SBO',
  'Total destruction of building',
  94,
  112,
  131
);

-- Insert for indicator 2
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'FR12',
  'Threshold of visible damage',
  53,
  65,
  80
), (
  2,
  'FR12',
  'Loss of roof covering material (<20%), gutters and/0r awning; loss of vinyl or metal siding',
  63,
  79,
  97
), (
  3,
  'FR12',
  'Broken clas in doors and windows',
  79,
  96,
  114
), (
  4,
  'FR12',
  'Uplift of roof deck and loss of significant roof covering material (>20%); collaps of chimney; garage doors collapse inward; failure of porch or carport',
  81,
  97,
  116
), (
  5,
  'FR12',
  'Entire house shifts off foundation',
  103,
  121,
  141
), (
  6,
  'FR12',
  'Large sections of roof structure removed; most walls remain standing',
  104,
  122,
  142
), (
  7,
  'FR12',
  'External walls collapsed',
  113,
  132,
  153
), (
  8,
  'FR12',
  'Most walls collapsed, except small interior rooms',
  127,
  152,
  178
), (
  9,
  'FR12',
  'All walls',
  142,
  170,
  198
), (
  10,
  'FR12',
  'Destruction of engineered and/or well constructed residence; slab swept clean',
  165,
  200,
  220
);

-- Insert for indicator 3
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'MHSW',
  'Threshold of visible damage',
  51,
  61,
  76
), (
  2,
  'MHSW',
  'Loss of shingles or partial uplift of one-piece metal roof covering',
  61,
  74,
  92
), (
  3,
  'MHSW',
  'Unit slides off block piers but remains upright',
  72,
  87,
  103
), (
  4,
  'MHSW',
  'Complete uplift of roof; most walls remain standing',
  73,
  89,
  112
), (
  5,
  'MHSW',
  'Unit rolls on its side or upside down; remains essentially intact',
  84,
  98,
  114
), (
  6,
  'MHSW',
  'Destruction of roof and walls leaving floor and undercarriage in place',
  87,
  105,
  123
), (
  7,
  'MHSW',
  'Unit rolls or vaults; roof and walls separate from floor and undercarriage',
  96,
  109,
  128
), (
  8,
  'MHSW',
  'Undercarriage separates from unit; rolls, tumbles and is badly bent',
  101,
  118,
  136
), (
  9,
  'MHSW',
  'Complete destruction of unit; debris blown away',
  110,
  127,
  148
);

-- Insert for indicator 4
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'MHDW',
  'Threshold of visible damage',
  51,
  61,
  76
), (
  2,
  'MHDW',
  'Loss of shingles or other roof covering (<20%)',
  62,
  76,
  88
), (
  3,
  'MHDW',
  'Damanged porches or carports',
  67,
  78,
  96
), (
  4,
  'MHDW',
  'Broken Windows',
  68,
  83,
  95
), (
  5,
  'MHDW',
  'Uplift of roof deck and loss of significant roof covering material (>20%)',
  75,
  88,
  108
), (
  6,
  'MHDW',
  'Complete uplift of roof; most walls remain standing',
  77,
  93,
  110
), (
  7,
  'MHDW',
  'Unit slides off CMU block piers',
  94,
  78,
  109
), (
  8,
  'MHDW',
  'Removal of entire roof structure leaving most walls standing',
  80,
  97,
  117
), (
  9,
  'MHDW',
  'Complete destruction of roof and walls leaving undercarriage in place',
  93,
  113,
  131
), (
  10,
  'MHDW',
  'Unit rolls, displaces or vaults',
  82,
  114,
  130
), (
  11,
  'MHDW',
  'Undercarriage separates from floor, rolls, tumbles, badly bent',
  109,
  127,
  145
), (
  12,
  'MHDW',
  'Complete destruction of unit; debris blows away',
  119,
  134,
  154
);

-- Insert for indicator 5
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'ACT',
  'Threshold of visible damage',
  63,
  76,
  95
), (
  2,
  'ACT',
  'Loss of roof covering (<20%)',
  82,
  99,
  121
), (
  3,
  'ACT',
  'Uplift of roof decking; significant loss of roof covering (>20%)',
  107,
  124,
  146
), (
  4,
  'ACT',
  'Uplift or collapse of roof structure leaving most walls standing',
  120,
  138,
  158
), (
  5,
  'ACT',
  'Most top story walls collapsed',
  138,
  158,
  184
), (
  6,
  'ACT',
  'Almost total destruction of top two stories',
  155,
  180,
  205
), (
  7,
  'ACT',
  'Total destruction of entire building',
  155,
  180,
  205
);

-- Insert for indicator 6
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'M',
  'Threshold of visible damage',
  54,
  66,
  83
), (
  2,
  'M',
  'Loss of roof covering (<20%)',
  67,
  80,
  99
), (
  3,
  'M',
  'Broken windows or patio doors',
  74,
  89,
  107
), (
  4,
  'M',
  'Uplift of roof decking; significant loss of roof covering (>20%); loss of EIFS wall cladding',
  80,
  95,
  116
), (
  5,
  'M',
  'Uplift or collapse of canopy over driveway',
  81,
  99,
  118
), (
  6,
  'M',
  'Uplift or collapse of roof structure leaving most walls standing',
  103,
  123,
  143
), (
  7,
  'M',
  'Collapse of top story exterior walls',
  121,
  138,
  156
), (
  8,
  'M',
  'Collapse of most top story walls',
  127,
  143,
  162
), (
  9,
  'M',
  'Collapse of top two floors of three or more stories',
  144,
  170,
  185
), (
  10,
  'M',
  'Total destruction of entire building',
  163,
  190,
  217
);

-- Insert for indicator 7
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'MAM',
  'Threshold of visible damage',
  54,
  65,
  81
), (
  2,
  'MAM',
  'Loss of roof covering (<20%)',
  67,
  80,
  101
), (
  3,
  'MAM',
  'Uplift of lightweight metal roof decking',
  81,
  95,
  116
), (
  4,
  'MAM',
  'Uplift of pre-case or cast-in-place concrete roof decking',
  103,
  121,
  143
), (
  5,
  'MAM',
  'Collapse of top story walls',
  115,
  133,
  150
), (
  6,
  'MAM',
  'Collapse of top two floors of three or more stories',
  132,
  156,
  180
), (
  7,
  'MAM',
  'Total destruction of a large section of building',
  160,
  180,
  205
);

-- Insert for indicator 8
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'SRB',
  'Threshold of visible damage',
  54,
  65,
  81
), (
  2,
  'SRB',
  'Loss of roof covering (<20%)',
  65,
  78,
  98
), (
  3,
  'SRB',
  'Broken glass in windows and doors',
  72,
  86,
  103
), (
  4,
  'SRB',
  'Uplift of roof decking; significant loss of roof covering (>20%)',
  81,
  98,
  119
), (
  5,
  'SRB',
  'Canopies or covered walkways destroyed',
  83,
  98,
  114
), (
  6,
  'SRB',
  'Uplift or collapse of entire roof structure',
  101,
  119,
  140
), (
  7,
  'SRB',
  'Collapse of exterior walls; closely spaced interior walls remain standing',
  120,
  138,
  159
), (
  8,
  'SRB',
  'Total destruction of entire building',
  143,
  167,
  193
);

-- Insert for indicator 9
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'SPB',
  'Threshold of visible damage',
  54,
  65,
  81
), (
  2,
  'SPB',
  'Loss of roof covering (<20%)',
  65,
  78,
  98
), (
  3,
  'SPB',
  'Broken windows, including clear story windows or skylights',
  74,
  89,
  107
), (
  4,
  'SPB',
  'Exterior doors fail',
  82,
  100,
  118
), (
  5,
  'SPB',
  'Uplift of roof decking; significant loss of roof covering (>20%); 
loss of rooftop HVAC equipment',
  84,
  100,
  117
), (
  6,
  'SPB',
  'Collapsed façade or parapet walls',
  85,
  103,
  123
), (
  7,
  'SPB',
  'Uplift or collapse of entire roof structure',
  105,
  124,
  145
), (
  8,
  'SPB',
  'Collapse of exterior walls; closely spaced interior walls  remain standing',
  123,
  144,
  165
), (
  9,
  'SPB',
  'Total destruction of entire building',
  148,
  157,
  200
);

-- Insert for indicator 10
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'SM',
  'Threshold of visible damage',
  54,
  65,
  81
), (
  2,
  'SM',
  'Uplift of roof covering at eaves and roof corners',
  66,
  80,
  100
), (
  3,
  'SM',
  'Broken windows or glass doors',
  72,
  80,
  100
), (
  4,
  'SM',
  'Uplift of  roof decking',
  84,
  101,
  122
), (
  5,
  'SM',
  'Collapsed façade or parapet walls',
  85,
  103,
  125
), (
  6,
  'SM',
  'Covered walkways uplifted or collapsed ',
  86,
  103,
  125
), (
  7,
  'SM',
  'Uplift or collapse of entire roof structure',
  103,
  122,
  143
), (
  8,
  'SM',
  'Collapse of exterior walls; closely spaced interior walls remain standing ',
  117,
  140,
  165
), (
  9,
  'SM',
  'Complete destruction of all or a large section of building',
  147,
  171,
  198
);

-- Insert for indicator 11
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'LSM',
  'Threshold of visible damage',
  59,
  71,
  86
), (
  2,
  'LSM',
  'Loss of roof covering (<20%)',
  69,
  85,
  105
), (
  3,
  'LSM',
  'Broken skylights, clearstory windows and atrium walls broken',
  75,
  92,
  114
), (
  4,
  'LSM',
  'Uplift of some roof decking; significant loss of roofing material (>20%); loss of rooftop HVAC',
  92,
  108,
  128
), (
  5,
  'LSM',
  'Wall cladding stripped starting at corners and progressing to other areas',
  111,
  94,
  131
), (
  6,
  'LSM',
  'Roof structure uplifted or collapsed ',
  108,
  128,
  150
), (
  7,
  'LSM',
  'Exterior walls in top story collapsed',
  124,
  144,
  166
), (
  8,
  'LSM',
  'Interior walls of top story collapse ',
  160,
  139,
  185
), (
  9,
  'LSM',
  'Complete destruction of all or a large section of the building',
  176,
  204,
  247
);

-- Insert for indicator 12 (Reversed EXP and LB in NOAA data)
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'LIRB',
  'Threshold of visible damage',
  54,
  57,
  83
), (
  2,
  'LIRB',
  'Loss of roof covering (<20%)',
  65,
  68,
  103
), (
  3,
  'LIRB',
  'Uplift of some roof decking; significant loss of roofing material (>20%); loss of rooftop HVAC',
  71,
  87,
  123
), (
  4,
  'LIRB',
  'Long roof spans collapsed downward',
  83,
  103,
  144
), (
  5,
  'LIRB',
  'Uplift and removal of roof structure',
  85,
  114,
  157
), (
  6,
  'LIRB',
  'Inward or outward collapse of exterior  walls',
  98,
  118,
  158
), (
  7,
  'LIRB',
  'Complete destruction of all or a large section of the building',
  110,
  147,
  201
);

-- Insert for indicator 13
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'ASR',
  'Threshold of visible damage',
  47,
  65,
  80
), (
  2,
  'ASR',
  'Loss of roof covering (<20%)',
  67,
  80,
  101
), (
  3,
  'ASR',
  'Broken glass in windows or doors',
  87,
  71,
  106
), (
  4,
  'ASR',
  'Uplift of some roof decking; significant loss of roofing material (>20%); loss of rooftop HVAC',
  83,
  101,
  120
), (
  5,
  'ASR',
  'Cladding stripped off walls',
  94,
  112,
  132
), (
  6,
  'ASR',
  'Uplift or collapse of roof structure ',
  98,
  118,
  140
), (
  7,
  'ASR',
  'Exterior walls collapsed',
  106,
  126,
  148
), (
  8,
  'ASR',
  'Complete destruction of all or a large section of the building',
  138,
  157,
  181
);

-- Insert for indicator 14
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'ASB',
  'Threshold of visible damage',
  46,
  63,
  79
), (
  2,
  'ASB',
  'Loss of roof covering (<20%)',
  65,
  78,
  99
), (
  3,
  'ASB',
  'Failure of large overhead doors',
  77,
  91,
  110
), (
  4,
  'ASB',
  'Uplift of some roof decking; significant loss of roofing material (>20%); loss of rooftop HVAC',
  80,
  98,
  119
), (
  5,
  'ASB',
  'Collapse of non-bearing masonry or tilt-up walls',
  94,
  114,
  134
), (
  6,
  'ASB',
  'Uplift or collapse of roof structure',
  102,
  121,
  143
), (
  7,
  'ASB',
  'Collapse of load-bearing walls',
  106,
  128,
  149
), (
  8,
  'ASB',
  'Complete destruction of all or a large section of the building',
  138,
  157,
  181
);

-- Insert for indicator 15
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'ES',
  'Threshold of visible damage',
  46,
  65,
  80
), (
  2,
  'ES',
  'Loss of roof covering (<20%)',
  66,
  79,
  99
), (
  3,
  'ES',
  'Broken windows',
  71,
  87,
  106
), (
  4,
  'ES',
  'Exterior door failures',
  85,
  99,
  118
), (
  5,
  'ES',
  'Uplift of some roof decking; significant loss of roofing material (>20%); loss of rooftop HVAC',
  82,
  101,
  121
), (
  6,
  'ES',
  'Damage to or loss of wall cladding',
  92,
  108,
  148
), (
  7,
  'ES',
  'Uplift or collapse of roof structure',
  108,
  125,
  148
), (
  8,
  'ES',
  'Collapse of non-bearing walls',
  117,
  139,
  162
), (
  9,
  'ES',
  'Collapse of load-bearing walls',
  130,
  153,
  180
), (
  10,
  'ES',
  'Total destruction of a large section of building or entire building',
  152,
  176,
  203
);

-- Insert for indicator 16
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'JHSH',
  'Threshold of visible damage',
  55,
  68,
  83
), (
  2,
  'JHSH',
  'Threshold of visible damage',
  66,
  79,
  99
), (
  3,
  'JHSH',
  'Threshold of visible damage',
  71,
  87,
  106
), (
  4,
  'JHSH',
  'Threshold of visible damage',
  83,
  101,
  121
), (
  5,
  'JHSH',
  'Threshold of visible damage',
  85,
  101,
  191
), (
  6,
  'JHSH',
  'Threshold of visible damage',
  92,
  108,
  127
), (
  7,
  'JHSH',
  'Threshold of visible damage',
  94,
  114,
  136
), (
  8,
  'JHSH',
  'Threshold of visible damage',
  108,
  125,
  136
), (
  9,
  'JHSH',
  'Threshold of visible damage',
  121,
  139,
  153
), (
  10,
  'JHSH',
  'Threshold of visible damage',
  133,
  158,
  186
), (
  11,
  'JHSH',
  'Threshold of visible damage',
  163,
  192,
  224
), ( -- Missing from NOAA data, filled with data from row above
  12,
  'JHSH',
  'Threshold of visible damage',
  163,
  192,
  224
);

-- Insert for indicator 17
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'LRB',
  'Threshold of visible damage',
  55,
  68,
  83
), (
  2,
  'LRB',
  'Loss of roof covering (<20%)',
  67,
  80,
  103
), (
  3,
  'LRB',
  'Uplift of metal roof decking at eaves and roof corners: significant loss of roofing material (>20%)',
  83,
  101,
  120
), (
  4,
  'LRB',
  'Broken glass in windows, entryways or atriums',
  83,
  101,
  122
), (
  5,
  'LRB',
  'Uplift of lightweight roof structure',
  114,
  133,
  157
), (
  6,
  'LRB',
  'Significant damage to exterior walls and some interior walls',
  122,
  143,
  167
), (
  7,
  'LRB',
  'Complete destruction of all or a large section of building',
  161,
  188,
  221
);

-- Insert for indicator 18
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'MRB',
  'Threshold of visible damage',
  58,
  70,
  86
), (
  2,
  'MRB',
  'Threshold of visible damage',
  68,
  83,
  103
), (
  3,
  'MRB',
  'Threshold of visible damage',
  75,
  98,
  113
), (
  4,
  'MRB',
  'Threshold of visible damage',
  83,
  99,
  118
), (
  5,
  'MRB',
  'Threshold of visible damage',
  83,
  101,
  120
), (
  6,
  'MRB',
  'Threshold of visible damage',
  98,
  119,
  140
), (
  7,
  'MRB',
  'Threshold of visible damage',
  110,
  129,
  150
), (
  8,
  'MRB',
  'Threshold of visible damage',
  118,
  136,
  158
), (
  9,
  'MRB',
  'Threshold of visible damage',
  120,
  145,
  167
), (
  10,
  'MRB',
  'Threshold of visible damage',
  181,
  210,
  268
);

-- Insert for indicator 19
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'HRB',
  'Threshold of visible damage',
  58,
  70,
  86
), (
  2,
  'HRB',
  'Loss of roof covering (<20%)',
  69,
  86,
  107
), (
  3,
  'HRB',
  'Damage to penthouse roof and walls; loss of rooftop HVAC equipment',
  75,
  86,
  107
), (
  4,
  'HRB',
  'Broken glass in exterior walls at 1t and 2nd floors; broken glass in entryways',
  83,
  101,
  120
), (
  5,
  'HRB',
  'Damage to parapet walls or coping',
  87,
  104,
  122
), (
  6,
  'HRB',
  'Broken curtain wall panel anchors',
  110,
  129,
  157
), (
  7,
  'HRB',
  'Significant loss of roofing material (>20%)',
  115,
  143,
  165
), (
  8,
  'HRB',
  'Significant damage to curtain walls and interior walls',
  123,
  145,
  172
), (
  9,
  'HRB',
  'Uplift or collapse of roof structure',
  123,
  159,
  183
), (
  10,
  'HRB',
  'Significant structural deformation',
  190,
  228,
  290
);

-- Insert for indicator 20
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'IB',
  'Threshold of visible damage',
  59,
  72,
  88
), (
  2,
  'IB',
  'Threshold of visible damage',
  72,
  86,
  109
), (
  3,
  'IB',
  'Threshold of visible damage',
  75,
  92,
  115
), (
  4,
  'IB',
  'Threshold of visible damage',
  95,
  114,
  136
), (
  5,
  'IB',
  'Threshold of visible damage',
  97,
  118,
  140
), (
  6,
  'IB',
  'Threshold of visible damage',
  97,
  118,
  140
), (
  7,
  'IB',
  'Threshold of visible damage',
  110,
  131,
  152
), (
  8,
  'IB',
  'Threshold of visible damage',
  119,
  142,
  163
), (
  9,
  'IB',
  'Threshold of visible damage',
  58,
  70,
  86
), (
  10,
  'IB',
  'Threshold of visible damage',
  127,
  148,
  172
), (
  11,
  'IB',
  'Threshold of visible damage',
  178,
  210,
  268
);

-- Insert for indicator 21
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'MBS',
  'Threshold of visible damage',
  54,
  67,
  83
), (
  2,
  'MBS',
  'Inward or outward collapsed of overhead doors',
  75,
  89,
  108
), (
  3,
  'MBS',
  'Metal roof or wall panels pulled from the building',
  78,
  95,
  120
), (
  4,
  'MBS',
  'Column anchorage failed',
  96,
  117,
  135
), (
  5,
  'MBS',
  'Buckling of roof purlins',
  95,
  118,
  138
), (
  6,
  'MBS',
  'Failure of X-braces in the lateral load resisting system',
  118,
  138,
  158
), (
  7,
  'MBS',
  'Progressive collapse of rigid frames',
  120,
  143,
  168
), (
  8,
  'MBS',
  'Total destruction of building',
  132,
  155,
  178
);

-- Insert for indicator 22
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'SSC',
  'Threshold of visible damage',
  45,
  63,
  79
), (
  2,
  'SSC',
  'Fascia material blown from canopy',
  64,
  78,
  96
), (
  3,
  'SSC',
  'Metal roof panels stripped from canopy',
  74,
  92,
  113
), (
  4,
  'SSC',
  'Columns bend or buckle under wind load',
  88,
  109,
  135
), (
  5,
  'SSC',
  'Canopy collapsed due to column foundation failure',
  90,
  114,
  144
), (
  6,
  'SSC',
  'Complete destruction of canopy',
  110,
  133,
  163
);

-- Insert for indicator 23
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'WHB',
  'Threshold of visible damage',
  55,
  68,
  83
), (
  2,
  'WHB',
  'Loss of roofing material (<20%)',
  69,
  83,
  105
), (
  3,
  'WHB',
  'Inward or outward collapse of overhead doors',
  75,
  88,
  107
), (
  4,
  'WHB',
  'Uplift of roof deck; significant loss of roofing material (>20%); loss of rooftop HVAC equipment',
  88,
  103,
  122
), (
  5,
  'WHB',
  'Collapse of other non-bearing exterior walls',
  93,
  114,
  126
), (
  6,
  'WHB',
  'Collapse of pre-cast concrete tilt-up panels',
  102,
  124,
  144
), (
  7,
  'WHB',
  'Total destruction of a large section of building or entire building',
  131,
  158,
  186
);

-- Insert for indicator 24
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'ETL',
  'Threshold of visible damage',
  70,
  83,
  98
), (
  2,
  'ETL',
  'Broken wood cross member',
  80,
  99,
  114
), (
  3,
  'ETL',
  'Wood poles leaning',
  85,
  108,
  130
), (
  4,
  'ETL',
  'Broken wood poles',
  98,
  118,
  142
), (
  5,
  'ETL',
  'Broken or bent steel or concrete poles',
  115,
  138,
  149
), (
  6,
  'ETL',
  'Collapsed metal truss towers',
  116,
  141,
  165
);

-- Insert for indicator 25
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'FST',
  'Threshold of visible damage',
  76,
  92,
  113
), (
  2,
  'FST',
  'Collapsed cell-phone tower',
  113,
  133,
  157
), (
  3,
  'FST',
  'Collapsed micro-wave tower',
  116,
  136,
  160
);

-- Insert for indicator 26
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'FSP',
  'Threshold of visible damage',
  67,
  81,
  100
), (
  2,
  'FSP',
  'Bent pole',
  85,
  102,
  120
), (
  3,
  'FSP',
  'Collapsed pole',
  99,
  118,
  138
);

-- Insert for indicator 27
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'TH',
  'Small limbs broken  (up to 1” diameter)',
  48,
  60,
  72
), (
  2,
  'TH',
  'Large branches broken  (1”-3” diameter)',
  61,
  74,
  88
), (
  3,
  'TH',
  'Trees uprooted',
  76,
  91,
  118
), (
  4,
  'TH',
  'Trunks snapped',
  93,
  110,
  134
), (
  5,
  'TH',
  'Trees debarked with only stubs of largest branches remaining',
  123,
  143,
  167
);

-- Insert for indicator 28
INSERT INTO tornado_degree_of_damage VALUES (
  1,
  'TS',
  'Small limbs broken  (up to 1” diameter)',
  48,
  60,
  72
), (
  2,
  'TS',
  'Large branches broken  (1” – 3” diameter)',
  62,
  75,
  88
), (
  3,
  'TS',
  'Trees uprooted',
  73,
  87,
  113
), (
  4,
  'TS',
  'Trunks snapped',
  88,
  104,
  128
), (
  5,
  'TS',
  'Trees debarked with only stubs of largest branches remaining',
  112,
  131,
  153
);

COMMIT;

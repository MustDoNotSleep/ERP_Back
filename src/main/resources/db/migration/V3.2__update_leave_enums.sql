-- leaves 테이블의 type과 duration ENUM 확장

-- 1. type ENUM에 새로운 휴가 종류 추가
ALTER TABLE leaves 
MODIFY COLUMN type ENUM(
    'ANNUAL',           -- 연차
    'SICK',             -- 병가
    'SICK_PAID',        -- 유급병가
    'MATERNITY',        -- 출산휴가
    'PATERNITY',        -- 배우자출산휴가
    'CHILDCARE',        -- 육아휴직
    'MARRIAGE',         -- 결혼휴가
    'FAMILY_MARRIAGE',  -- 가족결혼휴가
    'BEREAVEMENT',      -- 경조사
    'OFFICIAL',         -- 공가
    'UNPAID'            -- 무급휴가
) NOT NULL COMMENT '휴가 종류';

-- 2. duration ENUM에 오전/오후 구분 추가
ALTER TABLE leaves 
MODIFY COLUMN duration ENUM(
    'FULL_DAY',         -- 종일
    'HALF_DAY_AM',      -- 오전 반차
    'HALF_DAY_PM',      -- 오후 반차
    'QUARTER_DAY_AM',   -- 오전 반반차
    'QUARTER_DAY_PM',   -- 오후 반반차
    'HALF_DAY',         -- 기존 값 (하위 호환성)
    'QUARTER_DAY'       -- 기존 값 (하위 호환성)
) NOT NULL COMMENT '휴가 단위';

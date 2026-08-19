CREATE TABLE IF NOT EXISTS public.geoquest_submissions
(
    id int NOT NULL,
    quest_survey_submission_id uuid,
    creator_id uuid,
    last_modifier_id uuid,
    location_id character varying(255),
    location_wkt character varying(255),
    status character varying(255),
    submission_data character varying(4096),
    creation_time timestamp with time zone,
    last_modification_time timestamp with time zone,
    coordinate geometry(Point,4326),
    report_type character varying(255),
    user_name character varying(255),
    assigned_score int,
    image_count int,
    CONSTRAINT geoquest_submissions_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS public.geoquest_submissions_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 10000
    CACHE 1;
    
CREATE OR REPLACE FUNCTION ST_CS6DataToGeoJson()
RETURNS jsonb AS
$BODY$
    SELECT jsonb_build_object(
        'type',     'FeatureCollection',
        'features', jsonb_agg(feature)
    )
    FROM (
      SELECT jsonb_build_object(
        'type',       'Feature',
        'id',         row.id,
        'geometry',   ST_AsGeoJSON(coordinate)::jsonb,
        'properties', to_jsonb(row) - 'id' - 'coordinate' - 'name'
      ) AS feature
      FROM (SELECT * FROM public.geoquest_submissions) row) features;
$BODY$
LANGUAGE SQL;
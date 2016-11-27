CREATE KEYSPACE flickrassistant WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE flickrassistant;

CREATE TABLE app_user_details (
    nsid varchar,
    detail_key varchar,
    detail_value varchar,
    PRIMARY KEY ((nsid), detail_key)
);

CREATE TABLE dashboards (
    nsid varchar,
    dashboard_id uuid,
    created_at timestamp,
    PRIMARY KEY ((nsid), dashboard_id)
);

CREATE TYPE photo_urls (
    large_square_thumb varchar,
    large_thumb varchar,
    small varchar,
    large varchar
);

CREATE TABLE contacts (
    dashboard_id uuid,
    contact_of varchar,
    nsid varchar,
    username varchar,
    PRIMARY KEY ((dashboard_id), contact_of, nsid)
);

CREATE TABLE favourites (
    dashboard_id uuid,
    photo_id varchar,
    faved_by varchar,
    date_faved varchar,
    title varchar,
    owner varchar,
    owner_name varchar,
    date_upload varchar,
    date_taken varchar,
    count_views int,
    count_faves int,
    count_comments int,
    tags varchar,
    machine_tags varchar,
    urls map<varchar, varchar>,
    PRIMARY KEY ((dashboard_id), faved_by, photo_id)
);

-- phantom does not support it yet:(
-- urls frozen <photo_urls>,

CREATE TABLE photos (
    dashboard_id uuid,
    photo_id varchar,
    title varchar,
    owner varchar,
    owner_name varchar,
    date_upload varchar,
    date_taken varchar,
    count_views int,
    count_faves int,
    count_comments int,
    tags varchar,
    machine_tags varchar,
    urls map<varchar, varchar>,
    PRIMARY KEY ((dashboard_id), owner, photo_id)
);

CREATE TABLE photo_favourites (
    dashboard_id uuid,
    photo_id varchar,
    owner varchar,
    faved_by varchar,
    username varchar,
    realname varchar,
    date_faved varchar,
    PRIMARY KEY ((dashboard_id), owner, photo_id, faved_by)
);

CREATE TABLE relatives (
    dashboard_id uuid,
    nsid varchar,
    username varchar,
    realname varchar,
    followed boolean,
    faved int,
    faving int,
    contacts int,
    photos int,
    avg_points double,
    top_avg_points double,
    top_tags list<varchar>,
    PRIMARY KEY ((dashboard_id), nsid)
);

CREATE TABLE timeline (
    dashboard_id uuid,
    month varchar,
    uploaded int,
    faved int,
    got_favs int,
    PRIMARY KEY ((dashboard_id), month)
);

CREATE TABLE faving_users (
    dashboard_id uuid,
    user varchar,
    username varchar,
    realname varchar,
    count int,
    first_fav varchar,
    last_fav varchar,
    PRIMARY KEY ((dashboard_id), user, count)
);

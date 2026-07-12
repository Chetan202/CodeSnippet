-- Run this in Supabase SQL Editor before deploying this OAuth/share-link update.
-- It removes email verification, notification settings, tags, folders, and comments.
-- It keeps public share links, soft deletes, audit columns, snippet versions, and bookmarks.

alter table users
    drop column if exists verification_token,
    drop column if exists verification_token_expires_at,
    drop column if exists is_verified,
    drop column if exists notify_comments,
    drop column if exists notify_approvals,
    drop column if exists notify_product_updates;

alter table snippets
    add column if not exists share_token varchar(255),
    add column if not exists public_snippet boolean not null default false,
    add column if not exists updated_at timestamp,
    add column if not exists deleted_at timestamp,
    add column if not exists created_by varchar(255),
    add column if not exists updated_by varchar(255),
    add column if not exists deleted boolean not null default false,
    drop column if exists tags,
    drop column if exists collection_name;

alter table snippet_versions
    drop column if exists tags,
    drop column if exists collection_name;

drop table if exists comments cascade;
drop table if exists snippet_tags cascade;
drop table if exists tags cascade;

create unique index if not exists uk_snippets_share_token
    on snippets (share_token)
    where share_token is not null;

create table if not exists bookmarks (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    snippet_id bigint not null references snippets(id) on delete cascade,
    created_at timestamp default now(),
    constraint uk_bookmark_user_snippet unique (user_id, snippet_id)
);

create index if not exists idx_bookmarks_user_created
    on bookmarks(user_id, created_at desc);

CREATE TABLE room
(
    room_id          BIGSERIAL PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    room_type        VARCHAR(20)  NOT NULL,
    max_participants INTEGER      NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_room_type CHECK (room_type IN ('ONE_TO_ONE', 'GROUP')),
    CONSTRAINT chk_room_status CHECK (status IN ('ACTIVE', 'DELETED')),
    CONSTRAINT chk_max_participants CHECK (max_participants >= 2 AND max_participants <= 100)
);

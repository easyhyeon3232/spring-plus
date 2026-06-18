package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo todo = queryFactory
                .selectFrom(QTodo.todo)
                .leftJoin(QTodo.todo.user).fetchJoin()
                .where(QTodo.todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(todo);
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(
            String title,
            String managerNickname,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable
    ) {
        List<TodoSearchResponse> content = queryFactory
                .select(Projections.constructor(
                        TodoSearchResponse.class,
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(
                        titleContains(title),
                        managerNicknameContains(managerNickname),
                        createdAtGoe(startDateTime),
                        createdAtLoe(endDateTime)
                )
                .groupBy(todo.id, todo.title, todo.createdAt)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.id.countDistinct())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(
                        titleContains(title),
                        managerNicknameContains(managerNickname),
                        createdAtGoe(startDateTime),
                        createdAtLoe(endDateTime)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title)
                ? todo.title.containsIgnoreCase(title)
                : null;
    }

    private BooleanExpression managerNicknameContains(String managerNickname) {
        return StringUtils.hasText(managerNickname)
                ? user.nickname.containsIgnoreCase(managerNickname)
                : null;
    }

    private BooleanExpression createdAtGoe(LocalDateTime startDateTime) {
        return startDateTime != null
                ? todo.createdAt.goe(startDateTime)
                : null;
    }

    private BooleanExpression createdAtLoe(LocalDateTime endDateTime) {
        return endDateTime != null
                ? todo.createdAt.loe(endDateTime)
                : null;
    }
}

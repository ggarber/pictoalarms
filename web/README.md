# PictoAlarms — Web Dashboard

The web dashboard for **PictoAlarms**, a service that lets caregivers send pictogram-based alarms directly to the Wear OS smartwatches of users with special needs (autism, intellectual disabilities, communication challenges, etc.).

## Features

- Landing page explaining the service
- Caregiver dashboard to create and send pictogram alarms
- Team management — share access with family, teachers, and therapists
- Role-based access control (Owner / Member)
- Email/password authentication with JWT sessions
- Activity log for all alarm and team events

## Tech Stack

- **Framework**: [Next.js](https://nextjs.org/)
- **Database**: [Postgres](https://www.postgresql.org/) + [Drizzle ORM](https://orm.drizzle.team/)
- **UI**: [shadcn/ui](https://ui.shadcn.com/) + Tailwind CSS
- **Watch app**: Wear OS (see `../watch/`)

## Getting Started

```bash
cd web
pnpm install
```

### Set up the database

```bash
pnpm db:setup
pnpm db:migrate
pnpm db:seed
```

The seed creates a default account:

- **Email**: `test@test.com`
- **Password**: `admin123`

### Run locally

```bash
pnpm dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

## Environment Variables

Copy `.env.example` to `.env` and fill in the required values:

| Variable | Description |
|---|---|
| `POSTGRES_URL` | PostgreSQL connection string |
| `AUTH_SECRET` | Random secret for JWT signing (`openssl rand -base64 32`) |
| `BASE_URL` | Public base URL (e.g. `http://localhost:3000`) |

## Deploying

1. Push to GitHub and connect to [Vercel](https://vercel.com/).
2. Add the environment variables in Vercel project settings.
3. Make sure `POSTGRES_URL` points to your production database.

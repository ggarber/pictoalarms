import { Button } from '@/components/ui/button';
import { ArrowRight, Bell, Watch, Users, Heart } from 'lucide-react';
import Link from 'next/link';

export default function HomePage() {
  return (
    <main>
      {/* Hero */}
      <section className="py-20 bg-gradient-to-b from-blue-50 to-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="lg:grid lg:grid-cols-12 lg:gap-8 lg:items-center">
            <div className="sm:text-center md:max-w-2xl md:mx-auto lg:col-span-6 lg:text-left">
              <h1 className="text-4xl font-bold text-gray-900 tracking-tight sm:text-5xl md:text-6xl">
                Pictogram Alarms
                <span className="block text-blue-600">for Every Need</span>
              </h1>
              <p className="mt-4 text-lg text-gray-600 sm:text-xl">
                Send visual, pictogram-based alarms directly to the smartwatch
                of users with special needs — simple, clear, and always on
                their wrist.
              </p>
              <div className="mt-8 flex flex-col sm:flex-row gap-4 sm:justify-center lg:justify-start">
                <Link href="/sign-up">
                  <Button size="lg" className="rounded-full bg-blue-600 hover:bg-blue-700 text-white text-lg w-full sm:w-auto">
                    Get started free
                    <ArrowRight className="ml-2 h-5 w-5" />
                  </Button>
                </Link>
                <Link href="/sign-in">
                  <Button size="lg" variant="outline" className="rounded-full text-lg w-full sm:w-auto">
                    Sign in
                  </Button>
                </Link>
              </div>
            </div>

            <div className="mt-12 lg:mt-0 lg:col-span-6 flex justify-center">
              <div className="relative">
                {/* Watch mockup */}
                <div className="w-48 h-56 bg-gray-900 rounded-3xl shadow-2xl flex flex-col items-center justify-center border-4 border-gray-700">
                  <div className="w-36 h-36 bg-blue-100 rounded-2xl flex flex-col items-center justify-center gap-2 mb-2">
                    <span className="text-5xl">🍽️</span>
                    <span className="text-sm font-semibold text-blue-900 text-center leading-tight">
                      Lunch Time
                    </span>
                  </div>
                  <div className="flex gap-1 mt-1">
                    <div className="w-2 h-2 rounded-full bg-blue-500"></div>
                    <div className="w-2 h-2 rounded-full bg-gray-600"></div>
                    <div className="w-2 h-2 rounded-full bg-gray-600"></div>
                  </div>
                </div>
                {/* Strap */}
                <div className="absolute -top-6 left-1/2 -translate-x-1/2 w-12 h-8 bg-gray-700 rounded-t-lg"></div>
                <div className="absolute -bottom-6 left-1/2 -translate-x-1/2 w-12 h-8 bg-gray-700 rounded-b-lg"></div>
                {/* Notification bubble */}
                <div className="absolute -right-8 -top-4 bg-white rounded-xl shadow-lg px-3 py-2 text-xs font-semibold text-blue-700 border border-blue-100 whitespace-nowrap">
                  📳 New alarm sent!
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900">
              Designed for caregivers and their users
            </h2>
            <p className="mt-3 text-lg text-gray-500 max-w-2xl mx-auto">
              PictoAlarms bridges communication gaps by delivering clear,
              visual cues straight to a Wear OS smartwatch.
            </p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
            <FeatureCard
              icon={<Bell className="h-6 w-6" />}
              title="Pictogram alarms"
              description="Create alarms with universal pictograms so the message is instantly understood, no reading required."
            />
            <FeatureCard
              icon={<Watch className="h-6 w-6" />}
              title="Wear OS delivery"
              description="Alarms arrive directly on the user's smartwatch — always visible, always accessible."
            />
            <FeatureCard
              icon={<Users className="h-6 w-6" />}
              title="Multi-caregiver teams"
              description="Share access across family members, teachers, and therapists — everyone stays coordinated."
            />
            <FeatureCard
              icon={<Heart className="h-6 w-6" />}
              title="Built for inclusion"
              description="Tailored for people with autism, intellectual disabilities, and communication challenges."
            />
          </div>
        </div>
      </section>

      {/* How it works */}
      <section className="py-16 bg-blue-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900">How it works</h2>
          </div>
          <div className="grid sm:grid-cols-3 gap-8 text-center">
            <Step number="1" title="Create an alarm" description="Choose a pictogram and set the time or trigger it manually from your dashboard." />
            <Step number="2" title="Deliver to the watch" description="The alarm is sent instantly over the internet to the user's Wear OS smartwatch." />
            <Step number="3" title="Clear visual cue" description="The watch vibrates and displays the pictogram so the user knows exactly what to do." />
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 bg-blue-600">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl font-bold text-white sm:text-4xl">
            Ready to get started?
          </h2>
          <p className="mt-4 text-lg text-blue-100 max-w-xl mx-auto">
            Create your free account today and send your first pictogram alarm
            in minutes.
          </p>
          <div className="mt-8">
            <Link href="/sign-up">
              <Button size="lg" className="rounded-full bg-white text-blue-600 hover:bg-blue-50 text-lg font-semibold">
                Create free account
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
            </Link>
          </div>
        </div>
      </section>
    </main>
  );
}

function FeatureCard({
  icon,
  title,
  description
}: {
  icon: React.ReactNode;
  title: string;
  description: string;
}) {
  return (
    <div className="flex flex-col items-start">
      <div className="flex items-center justify-center h-12 w-12 rounded-xl bg-blue-600 text-white mb-4">
        {icon}
      </div>
      <h3 className="text-lg font-semibold text-gray-900 mb-2">{title}</h3>
      <p className="text-gray-500 text-sm leading-relaxed">{description}</p>
    </div>
  );
}

function Step({
  number,
  title,
  description
}: {
  number: string;
  title: string;
  description: string;
}) {
  return (
    <div className="flex flex-col items-center">
      <div className="flex items-center justify-center h-12 w-12 rounded-full bg-blue-600 text-white text-xl font-bold mb-4">
        {number}
      </div>
      <h3 className="text-lg font-semibold text-gray-900 mb-2">{title}</h3>
      <p className="text-gray-600 text-sm leading-relaxed">{description}</p>
    </div>
  );
}

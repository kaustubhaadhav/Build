'use client';

import { useEffect, useMemo, useState } from 'react';
import { buildSteps, nextStep, prevStep, type Step } from '@/flows/cancelMachine';
import type { AB } from '@/types/cancellation';

type CancellationRow = {
  id: string;
  ab_variant: AB;
  found_job: boolean | null;
  found_via_platform: boolean | null;
  visa: string | null;
  reason: string | null;
  willing_to_pay: number | null;
  feedback: string | null;
};

export default function CancelModal({ onClose }: { onClose: () => void }) {
  const [row, setRow] = useState<CancellationRow | null>(null);
  const [answers, setAnswers] = useState<Record<string, any>>({});
  const [current, setCurrent] = useState<Step>('found_or_not');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Start session
  useEffect(() => {
    (async () => {
      setBusy(true);
      setError(null);
      try {
        const res = await fetch('/api/cancellations/start', { method: 'POST' });
        const json = await res.json();
        setRow(json.cancellation);
      } catch (e: any) {
        setError('Failed to start cancellation. Please try again.');
      } finally {
        setBusy(false);
      }
    })();
  }, []);

  const steps = useMemo(
    () => (row ? buildSteps(row.ab_variant, { ...row, ...answers }) : ['found_or_not']),
    [row, answers]
  );

  const idx = steps.indexOf(current);
  const total = steps.length;

  async function save(partial: Record<string, any>) {
    if (!row) return;
    setBusy(true);
    setError(null);
    try {
      const res = await fetch(`/api/cancellations/${row.id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(partial),
      });
      const json = await res.json();
      setRow(json.cancellation);
      setAnswers((prev) => ({ ...prev, ...partial }));
    } catch (e: any) {
      setError('Could not save your answer. Check your connection and try again.');
    } finally {
      setBusy(false);
    }
  }

  async function complete() {
    if (!row) return;
    setBusy(true);
    setError(null);
    try {
      await fetch(`/api/cancellations/${row.id}/complete`, { method: 'POST' });
      onClose();
    } catch {
      setError('Completion failed. Please try again.');
    } finally {
      setBusy(false);
    }
  }

  function goNext() { setCurrent(nextStep(steps, current)); }
  function goBack() { setCurrent(prevStep(steps, current)); }

  // Simple responsive modal shell (Tailwind)
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white p-4 md:p-6 shadow-xl">
        <div className="mb-3 text-sm text-gray-500">{idx >= 0 ? `Step ${idx + 1} of ${total}` : ''}</div>

        {error && <div className="mb-3 rounded-md bg-red-50 p-3 text-sm text-red-700">{error}</div>}
        {busy && !row && <div className="py-12 text-center text-gray-500">Starting…</div>}

        {row && (
          <>
            {current === 'found_or_not' && (
              <FoundOrNot
                value={row.found_job}
                onSelect={async (v) => { await save({ found_job: v }); goNext(); }}
              />
            )}

            {current === 'congrats' && (
              <Congrats onNext={goNext} onBack={goBack} />
            )}

            {current === 'feedback_found' && (
              <Feedback
                label="What could we have done better?"
                initial={row.feedback}
                onBack={goBack}
                onNext={async (v) => { await save({ feedback: v }); goNext(); }}
              />
            )}

            {current === 'visa_followup' && (
              <VisaFollowup
                onBack={goBack}
                initialVisa={row.visa}
                onNext={async (visa) => { await save({ visa }); goNext(); }}
              />
            )}

            {current === 'end_found' && (
              <Completion
                title={row.found_via_platform ? 'Thanks, and congrats on your new role!' : 'All set—good luck on your next chapter!'}
                onDone={complete}
              />
            )}

            {current === 'downsell' && (
              <Downsell
                onBack={goBack}
                onAccept={async () => { /* record acceptance if you want */ goNext(); }}
                onSkip={() => goNext()}
              />
            )}

            {current === 'feedback_not_found' && (
              <Feedback
                label="What could we improve while you're still looking?"
                initial={row.feedback}
                onBack={goBack}
                onNext={async (v) => { await save({ feedback: v }); goNext(); }}
              />
            )}

            {current === 'reason' && (
              <ReasonStep
                initialReason={row.reason}
                initialFoundVia={row.found_via_platform}
                onBack={goBack}
                onNext={async (payload) => { await save(payload); goNext(); }}
              />
            )}

            {current === 'reason_followup' && (
              <PriceFollowup
                initial={row.willing_to_pay ?? undefined}
                onBack={goBack}
                onNext={async (n) => { await save({ willing_to_pay: n }); goNext(); }}
              />
            )}

            {current === 'end_not_found' && (
              <Completion title="Your subscription is canceled." onDone={complete} />
            )}
          </>
        )}

        <button className="absolute right-4 top-4 text-gray-400 hover:text-gray-600" onClick={onClose} aria-label="Close">✕</button>
      </div>
    </div>
  );
}

/* ===== Minimal step components (trimmed for brevity) ===== */
function FoundOrNot({ value, onSelect }: { value: boolean | null, onSelect: (v: boolean)=>void }) {
  return (
    <div>
      <h2 className="text-xl md:text-2xl font-semibold mb-2">Did you find a job?</h2>
      <p className="text-sm text-gray-600 mb-4">This helps us tailor the next steps.</p>
      <div className="flex gap-2">
        <button className="rounded-xl border px-4 py-2" onClick={() => onSelect(true)}>Yes</button>
        <button className="rounded-xl border px-4 py-2" onClick={() => onSelect(false)}>Not yet</button>
      </div>
    </div>
  );
}

function Congrats({ onNext, onBack }: { onNext: ()=>void; onBack: ()=>void }) {
  return (
    <div>
      <h2 className="text-xl md:text-2xl font-semibold mb-2">Congrats on the new role!</h2>
      <p className="text-sm text-gray-600 mb-4">A few quick questions before we wrap up.</p>
      <div className="flex justify-between">
        <button className="rounded-xl border px-4 py-2" onClick={onBack}>Back</button>
        <button className="rounded-xl bg-black text-white px-4 py-2" onClick={onNext}>Next</button>
      </div>
    </div>
  );
}

function Feedback({ label, initial, onNext, onBack }:{
  label: string; initial?: string|null; onNext:(v:string)=>void; onBack:()=>void;
}) {
  const [v, setV] = useState(initial ?? '');
  return (
    <div>
      <h2 className="text-xl md:text-2xl font-semibold mb-2">{label}</h2>
      <textarea className="w-full rounded-xl border p-3 min-h-[120px]" value={v} onChange={e=>setV(e.target.value)} />
      <div className="mt-3 flex justify-between">
        <button className="rounded-xl border px-4 py-2" onClick={onBack}>Back</button>
        <button className="rounded-xl bg-black text-white px-4 py-2" onClick={()=>onNext(v)} disabled={!v.trim()}>Next</button>
      </div>
    </div>
  );
}

function VisaFollowup({ initialVisa, onNext, onBack }:{
  initialVisa?: string|null; onNext:(v:string)=>void; onBack:()=>void;
}) {
  const [visa, setVisa] = useState(initialVisa ?? '');
  return (
    <div>
      <h2 className="text-xl md:text-2xl font-semibold mb-2">Which visa will you apply for?</h2>
      <select className="w-full rounded-xl border p-3" value={visa} onChange={e=>setVisa(e.target.value)}>
        <option value="" disabled>Select visa</option>
        {['H1B','H4','L1','TN','E3','Other'].map(v => <option key={v} value={v}>{v}</option>)}
      </select>
      <div className="mt-3 flex justify-between">
        <button className="rounded-xl border px-4 py-2" onClick={onBack}>Back</button>
        <button className="rounded-xl bg-black text-white px-4 py-2" onClick={()=>visa && onNext(visa)} disabled={!visa}>Next</button>
      </div>
    </div>
  );
}

function Downsell({ onAccept, onSkip, onBack }:{ onAccept:()=>void; onSkip:()=>void; onBack:()=>void }) {
  return (
    <div>
      <h2 className="text-xl md:text-2xl font-semibold mb-2">Keep access for a lower price?</h2>
      <p className="text-sm text-gray-600 mb-4">Try a limited plan for the next month.</p>
      <div className="flex flex-wrap gap-2">
        <button className="rounded-xl bg-black text-white px-4 py-2" onClick={onAccept}>Accept offer</button>
        <button className="rounded-xl border px-4 py-2" onClick={onSkip}>No thanks</button>
        <button className="ml-auto text-gray-500 underline" onClick={onBack}>Back</button>
      </div>
    </div>
  );
}

function ReasonStep({ initialReason, initialFoundVia, onNext, onBack }:{
  initialReason?: string|null; initialFoundVia?: boolean|null;
  onNext:(payload: Record<string,any>)=>void; onBack:()=>void;
}) {
  const [reason, setReason] = useState(initialReason ?? '');
  const [foundVia, setFoundVia] = useState<boolean | null>(initialFoundVia ?? null);

  const canNext = reason && (foundVia !== null);

  return (
    <div>
      <h2 className="text-xl md:text-2xl font-semibold mb-2">What's your main reason for canceling?</h2>
      <select className="w-full rounded-xl border p-3 mb-3" value={reason} onChange={e=>setReason(e.target.value)}>
        <option value="" disabled>Select reason</option>
        <option value="too_expensive">Too expensive</option>
        <option value="not_finding_jobs">Not finding good matches</option>
        <option value="low_quality">Quality concerns</option>
        <option value="not_using">Not using it</option>
        <option value="got_offer_elsewhere">Got offer elsewhere</option>
        <option value="other">Other</option>
      </select>

      <fieldset className="mb-3">
        <legend className="text-sm text-gray-700 mb-1">Did you find your job through us?</legend>
        <div className="flex gap-3">
          <button className={`rounded-xl border px-3 py-1 ${foundVia===true?'bg-gray-900 text-white':''}`} onClick={()=>setFoundVia(true)}>Yes</button>
          <button className={`rounded-xl border px-3 py-1 ${foundVia===false?'bg-gray-900 text-white':''}`} onClick={()=>setFoundVia(false)}>No</button>
        </div>
      </fieldset>

      <div className="flex justify-between">
        <button className="rounded-xl border px-4 py-2" onClick={onBack}>Back</button>
        <button className="rounded-xl bg-black text-white px-4 py-2" disabled={!canNext}
          onClick={()=>onNext({ reason, found_via_platform: foundVia })}>Next</button>
      </div>
    </div>
  );
}

function PriceFollowup({ initial, onNext, onBack }:{
  initial?: number; onNext:(n:number)=>void; onBack:()=>void;
}) {
  const [val, setVal] = useState(String(initial ?? ''));
  const n = Number(val);
  const valid = Number.isFinite(n) && n > 0 && n <= 999;
  return (
    <div>
      <h2 className="text-xl md:text-2xl font-semibold mb-2">What price would you be willing to pay?</h2>
      <input className="w-full rounded-xl border p-3" inputMode="numeric" value={val} onChange={e=>setVal(e.target.value)} placeholder="e.g., 19" />
      {!valid && val && <p className="mt-1 text-sm text-red-600">Enter a number between 1 and 999</p>}
      <div className="mt-3 flex justify-between">
        <button className="rounded-xl border px-4 py-2" onClick={onBack}>Back</button>
        <button className="rounded-xl bg-black text-white px-4 py-2" onClick={()=>valid && onNext(n)} disabled={!valid}>Next</button>
      </div>
    </div>
  );
}

function Completion({ title, onDone }:{ title: string; onDone:()=>void }) {
  return (
    <div className="text-center">
      <h2 className="text-xl md:text-2xl font-semibold mb-2">{title}</h2>
      <p className="text-sm text-gray-600 mb-4">Thanks for the feedback — it helps us improve.</p>
      <button className="rounded-xl bg-black text-white px-5 py-2" onClick={onDone}>Finish</button>
    </div>
  );
}

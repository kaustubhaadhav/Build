import { NextResponse } from 'next/server';
import { supabaseAdmin } from '@/lib/supabaseAdmin';

export async function POST() {
  const ab: 'A'|'B' = Math.random() < 0.5 ? 'A' : 'B';
  const { data, error } = await supabaseAdmin
    .from('cancellations')
    .insert({ ab_variant: ab })
    .select()
    .single();

  if (error) return NextResponse.json({ error: error.message }, { status: 500 });
  return NextResponse.json({ cancellation: data });
}

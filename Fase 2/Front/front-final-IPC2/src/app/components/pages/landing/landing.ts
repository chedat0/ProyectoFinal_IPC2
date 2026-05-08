import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Footer } from "../../shared/footer/footer";

@Component({
  selector: 'app-landing',
  imports: [Footer, CommonModule, RouterModule],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
})
export class Landing {
  features = [
    { icon: '🔍', title: 'Encuentra talento', desc: 'Publica tu proyecto y recibe propuestas de freelancers calificados en Guatemala.' },
    { icon: '🤝', title: 'Contrata con confianza', desc: 'Sistema de contratos, entregas y pagos seguros integrado en la plataforma.' },
    { icon: '💳', title: 'Pagos protegidos', desc: 'El pago se libera al freelancer solo cuando apruebas la entrega del trabajo.' },
    { icon: '⭐', title: 'Calificaciones reales', desc: 'Sistema de reseñas para que encuentres siempre al mejor profesional.' },
    { icon: '📊', title: 'Reportes y estadísticas', desc: 'Visualiza el historial de proyectos, gastos y rendimiento de tu negocio.' },
    { icon: '🛡️', title: 'Plataforma segura', desc: 'Autenticación por roles, datos protegidos y comisiones transparentes.' },
  ];

  steps = [
    { num: '01', role: 'Cliente', title: 'Publica tu proyecto', desc: 'Describe lo que necesitas, establece presupuesto y fecha límite.' },
    { num: '02', role: 'Freelancer', title: 'Envía tu propuesta', desc: 'Explora proyectos abiertos y postula con tu oferta y carta de presentación.' },
    { num: '03', role: 'Cliente', title: 'Acepta la mejor oferta', desc: 'Revisa propuestas, elige al freelancer ideal y se crea el contrato automáticamente.' },
    { num: '04', role: 'Ambos', title: 'Entrega y pago', desc: 'El freelancer entrega el trabajo, tú lo apruebas y el pago se libera de inmediato.' },
  ];
}
